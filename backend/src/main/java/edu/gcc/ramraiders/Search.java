package edu.gcc.ramraiders;

import java.util.*;

public class Search {

    private final Set<Course> results = new TreeSet<>();
    private final Set<Course> filteredResults = new TreeSet<>();
    private Filter currentFilter = null;
    private String currentQuery = "";

    public Search() {
        results.addAll(Main.courseDB.getCourseList());
        filteredResults.addAll(results);
    }

    /**
     * Updates the search results' query and filter and returns a set of courses
     * @param query The query (empty string is equivalent to the entire course list)
     * @param filter The filter (filters what is returned by the query)
     */
    public Set<Course> search(String query, Filter filter) {
        // Apply query
        if (!query.equals(currentQuery)) {
            results.clear();
            if (query.isBlank()) {
                results.addAll(Main.courseDB.getCourseList());
            } else {
                var words = Arrays.asList(query.split("\\s+"));
                for (Course course : Main.courseDB.getCourseList()) {
                    if (courseMatchesByWordSet(words, course)) {
                        results.add(course);
                    }
                }
            }
            currentQuery = query;
            // Trigger re-filtering
            filteredResults.clear();
            filteredResults.addAll(results);
            currentFilter = null;
        }
        // Apply filters
        if (!filter.equals(currentFilter)) {
            System.out.println("Current filter: " + filter);
            currentFilter = filter;
            filteredResults.removeIf(course -> {
                if ((!filter.departments().isEmpty() && !filter.departments().contains(course.department()))
                || (!filter.codes().isEmpty() && !filter.codes().contains(course.code()))
                || (!filter.semesters().isEmpty() && !filter.semesters().contains(course.semester()))
                || (!filter.years().isEmpty() && !filter.years().contains(course.year()))
                || (!filter.creditHours().isEmpty() && !filter.creditHours().contains(course.credits()))
                ) {
                    return true;
                }
                if (filter.isAvailable() && course.openSeats() == 0) {
                    return true;
                }
                boolean fitsAnyTimeslot = fitsAnyTimeslot(filter, course);
                return !fitsAnyTimeslot;
            });
        }
        return filteredResults;
    }

    private static boolean fitsAnyTimeslot(Filter filter, Course course) {
        if (filter.timeslots().isEmpty()) {
            return true;
        }
        boolean fitsAnyTimeslot = false;
        for (Filter.Timeslot timeslot : filter.timeslots()) {
            for (var meetingTime : course.meetingTimes()) {
                if (!timeslot.day().equals(meetingTime.day())) {
                    continue;
                }
                if (timeslot.length() < meetingTime.minutesLong()) {
                    continue;
                }
                var meetStartSecs = (meetingTime.hour() * 3600) + (meetingTime.minute() * 60);
                var meetEndSecs = meetStartSecs + meetingTime.minutesLong();
                var tsBeginSecs = (timeslot.hour() * 3600) + (timeslot.minute() * 60);
                var tsEndSecs = tsBeginSecs + timeslot.length();
                if (tsBeginSecs < meetStartSecs || meetEndSecs > tsEndSecs) {
                    continue;
                }
                fitsAnyTimeslot = true;
            }
        }
        return fitsAnyTimeslot;
    }

    private static boolean courseMatchesWord(int i, List<String> words, Course course) {
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
        if (word.length() == 1 && course.section() == word.charAt(0)) {
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
            } else if (hasEarly && course.semester() == Course.SemesterType.EarlySummer) {
                return true;
            }
        }
        if (word.equalsIgnoreCase("winter") && course.semester() == Course.SemesterType.WinterOnline) {
            return true;
        }

        // check for professor names
        for (var pn : course.professorNames()) {
            for (var t : pn.split("\\s+")) {
                if (t.length() > 1 && t.replaceAll("[^a-zA-Z0-9]", "").equalsIgnoreCase(word)) {
                    return true;
                }
            }
        }

        // check for meeting times
        for (var meetingTime : course.meetingTimes()) {
            if ((word.equalsIgnoreCase("monday") && meetingTime.day() == Course.Day.Monday)
                    || (word.equalsIgnoreCase("tuesday") && meetingTime.day() == Course.Day.Tuesday)
                    || (word.equalsIgnoreCase("wednesday") && meetingTime.day() == Course.Day.Wednesday)
                    || (word.equalsIgnoreCase("thursday") && meetingTime.day() == Course.Day.Thursday)
                    || (word.equalsIgnoreCase("friday") && meetingTime.day() == Course.Day.Friday)) {
                return true;
            }
            try {
                var timeParts = word.split(":");
                int hr = Integer.parseInt(timeParts[0]);
                int min = Integer.parseInt(timeParts[1]);
                if (meetingTime.hour() == hr || meetingTime.minute() == min) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private static boolean courseMatchesByWordSet(List<String> words, Course course) {
        int matchCount = 0;

        for (int i = 0; i < words.size(); i++) {
            if (courseMatchesWord(i, words, course)) {
                matchCount++;
            }
        }
        return matchCount >= words.size();
    }

}
