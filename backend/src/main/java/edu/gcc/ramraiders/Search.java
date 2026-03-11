package edu.gcc.ramraiders;

import java.util.*;

public class Search {

    private Set<Course> results;
    private Filter combinedFilter;
    private String currentQuery;

    public Search() {
        results = new HashSet<>();
        combinedFilter = null;
    }

    /**
     * @return The courses selected by the current filter
     */
    public Set<Course> getCourses() {
        return results;
    }

    private boolean courseMatchesByWordSet(List<String> words, Course course) {
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);

            // check for department
            if (course.department().contains(word)) {
                return true;
            }
            // check for code or year
            try {
                int codeOrYear = Integer.parseInt(word);
                if (course.code() == codeOrYear || course.year() == codeOrYear) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
            // check for section
            if (course.section() == word.charAt(0)) {
                return true;
            }
            // check for name
            if (course.name().contains(word)) {
                return true;
            }

            // check for semester
            if (word.equalsIgnoreCase("fall") && course.semester() == Course.SemesterType.Fall) {
                return true;
            }
            if (word.equalsIgnoreCase("spring") && course.semester() == Course.SemesterType.Spring) {
                return true;
            }
            if (word.equalsIgnoreCase("summer") && (course.semester() == Course.SemesterType.EarlySummer
                || course.semester() == Course.SemesterType.LateSummer)) {

                boolean hasLate = i > 0 && words.get(i - 1).equalsIgnoreCase("late");
                boolean hasEarly = i > 0 && words.get(i - 1).equalsIgnoreCase("early");

                // edge case: ambiguous summer class specified
                if (!hasLate && !hasEarly) {
                    return true;
                }
                // late or early was specified
                if (hasLate && course.semester() == Course.SemesterType.LateSummer) {
                    return true;
                }
                else if (hasEarly && course.semester() == Course.SemesterType.EarlySummer) {
                    return true;
                }
            }
            if (word.equalsIgnoreCase("winter") && course.semester() == Course.SemesterType.WinterOnline) {
                return true;
            }

            // check for professor names
            if (course.professorNames().contains(word)) {
                return true;
            }

            // check for meeting times
            for (var meetingTime : course.meetingTimes()) {
                if ((word.equalsIgnoreCase("monday") && meetingTime.day() == Course.Day.Monday)
                || (word.equalsIgnoreCase("tuesday") && meetingTime.day() == Course.Day.Tuesday)
                || (word.equalsIgnoreCase("wednesday") && meetingTime.day() == Course.Day.Wednesday)
                || (word.equalsIgnoreCase("thursday") && meetingTime.day() == Course.Day.Thursday)
                || (word.equalsIgnoreCase("friday") && meetingTime.day() == Course.Day.Friday)){
                    return true;
                }
                try {
                    var timeParts = word.split(":");
                    int hr = Integer.parseInt(timeParts[0]);
                    int min = Integer.parseInt(timeParts[1]);
                    if (meetingTime.hour() == hr || meetingTime.minute() == min) {
                        return true;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return false;
    }

    /**
     * Updates the search results' filter
     * @param filter The filter
     */
    public void applyFilter(String query, Filter filter) {

        // Reset results
        if (!query.equals(currentQuery)) {
            results.clear();
            var words = Arrays.asList(query.split("\\s+"));
            for (Course course : Main.courseDB.getCourseList()) {
                if (courseMatchesByWordSet(words, course)) {
                    results.add(course);
                }
            }
        }
        else {

        }

//        if (combinedFilter == null) {
//            combinedFilter = filter;
//        } else {
//            combinedFilter = combinedFilter.combine(filter);
//        }
//
//        // This is not the highest performance but make it work first
//        results.clear();
//        for (String dept : combinedFilter.departments()) {
//            results.addAll(Main.courseDB.getCoursesByDepartment(dept));
//        }
//        for (int codes : combinedFilter.codes()) {
//            results.addAll(Main.courseDB.getCoursesByCode(codes));
//        }

    }
}
