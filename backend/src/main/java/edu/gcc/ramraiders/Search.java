package edu.gcc.ramraiders;

import java.util.*;

public class Search {

    private final Set<Course> results = new TreeSet<>();
    private final Set<Course> filteredResults = new TreeSet<>();
    private Filter currentFilter = null;
    private String currentQuery = "";
    private final CourseDB courseDB;

    public Search(CourseDB courseDB) {
        results.addAll(courseDB.getCourseList());
        filteredResults.addAll(results);
        this.courseDB = courseDB;
    }


    /**
     * Updates the search results' query and filter and returns a set of courses
     *
     * @param query  The query (empty string is equivalent to the entire course list)
     * @param filter The filter (filters what is returned by the query)
     */
    public Set<Course> search(String query, Filter filter) {
        // Apply query
        if (!query.equals(currentQuery)) {
            updateResultsFromQuery(query);
        }

        // Apply filters
        if (filter != null && !filter.equals(currentFilter)) {
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

    private void updateResultsFromQuery(String query) {
        var words = List.of(query.split("\\s+"));
        var queriedDepartments = new HashSet<String>();
        var queriedCodes = new HashSet<Integer>();
        var queriedSections = new HashSet<Character>();
        var queriedYears = new HashSet<Integer>();
        var queriedNames = new HashSet<String>();
        var queriedProfessors = new HashSet<String>();
        var queriedSemesters = new HashSet<Course.SemesterType>();
        var queriedDays = new HashSet<Course.Day>();
        var queriedMeetingTimes = new HashSet<Course.MeetingTime>();

        results.clear();

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);

            // check for department
            boolean isDept = false;
            for (String dept : courseDB.getPossibleDepartments()) {
                if (word.equalsIgnoreCase(dept)) {
                    queriedDepartments.add(dept);
                    isDept = true;
                    break;
                }
            }
            if (isDept) {
                continue;
            }
            // check for code or year
            try {
                int codeOrYear = Integer.parseInt(word);
                if (courseDB.getPossibleYears().contains(codeOrYear)) {
                    queriedYears.add(codeOrYear);
                    continue;
                }
                queriedCodes.add(codeOrYear);
                continue;
            } catch (NumberFormatException ignored) {
            }
            // check for section
            if (word.length() == 1 && Character.isLetter(word.charAt(0))) {
                queriedSections.add(word.charAt(0));
                continue;
            }
            // check for professor name
            boolean isProf = false;
            for (var pn : courseDB.getPossibleProfessors()) {
                for (var t : pn.split("\\s+")) {
                    if (t.length() > 1 && t.replaceAll("[^a-zA-Z0-9]", "").equalsIgnoreCase(word)) {
                        queriedProfessors.add(pn);
                        isProf = true;
                    }
                }
            }
            if (isProf) {
                continue;
            }

            // check for semester
            if (word.equalsIgnoreCase("fall")) {
                queriedSemesters.add(Course.SemesterType.Fall);
                continue;
            }
            if (word.equalsIgnoreCase("spring")) {
                queriedSemesters.add(Course.SemesterType.Spring);
                continue;
            }
            if (word.equalsIgnoreCase("summer")) {
                boolean hasLate = i > 0 && words.get(i - 1).equalsIgnoreCase("late");
                boolean hasEarly = i > 0 && words.get(i - 1).equalsIgnoreCase("early");
                // edge case: ambiguous summer class specified
                if (!hasLate && !hasEarly) {
                    queriedSemesters.add(Course.SemesterType.EarlySummer);
                    queriedSemesters.add(Course.SemesterType.LateSummer);
                    continue;
                }
                // late or early was specified
                else if (hasLate) {
                    queriedSemesters.add(Course.SemesterType.LateSummer);
                } else {
                    queriedSemesters.add(Course.SemesterType.EarlySummer);
                }
                continue;
            }
            if (word.equalsIgnoreCase("winter")) {
                queriedSemesters.add(Course.SemesterType.WinterOnline);
            }

            // check for meeting days
            if (word.equalsIgnoreCase("monday")) {
                queriedDays.add(Course.Day.Monday);
                continue;
            }
            if (word.equalsIgnoreCase("tuesday")) {
                queriedDays.add(Course.Day.Tuesday);
                continue;
            }
            if (word.equalsIgnoreCase("wednesday")) {
                queriedDays.add(Course.Day.Wednesday);
                continue;
            }
            if (word.equalsIgnoreCase("thursday")) {
                queriedDays.add(Course.Day.Thursday);
                continue;
            }
            if (word.equalsIgnoreCase("friday")) {
                queriedDays.add(Course.Day.Friday);
                continue;
            }

            // check for meeting times
            if (word.matches("^([01][0-9]|2[0-3]):[0-5][0-9]$")) {
                try {
                    var timeParts = word.split(":");
                    int hr = Integer.parseInt(timeParts[0]);
                    int min = Integer.parseInt(timeParts[1]);
                    for (var day : queriedDays) {
                        queriedMeetingTimes.add(new Course.MeetingTime(day, hr, min, 0));
                    }
                    continue;
                } catch (Exception ignored) {
                }
            }

            // otherwise the query word must be for a course name
            if (word.matches("^[a-zA-Z0-9]+$")) {
                queriedNames.add(word);
            }
        }

        System.out.printf(
            """
            Queried depts: %s
            Queried codes: %s
            Queried sections: %s
            Queried names: %s
            Queried semesters: %s
            Queried years: %s
            Queried days: %s
            Queried meeting times: %s
            Queried professors: %s
            """,
            queriedDepartments, queriedCodes, queriedSections, queriedNames, queriedSemesters, queriedYears,
            queriedDays, queriedMeetingTimes, queriedProfessors);

        for (Course course : courseDB.getCourseList()) {
            if (!queriedDepartments.isEmpty() && !queriedDepartments.contains(course.department())) {
                continue;
            }
            if (!queriedCodes.isEmpty() && !queriedCodes.contains(course.code())) {
                continue;
            }
            if (!queriedSections.isEmpty() && !queriedSections.contains(course.section())) {
                continue;
            }
            if (!queriedSemesters.isEmpty() && !queriedSemesters.contains(course.semester())) {
                continue;
            }
            if (!queriedYears.isEmpty() && !queriedYears.contains(course.year())) {
                continue;
            }
            if (!queriedNames.isEmpty()) {
                boolean found = false;
                for (var name : queriedNames) {
                    if (name.toLowerCase().contains(course.name().toLowerCase())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            if (!queriedDays.isEmpty()) {
                boolean found = false;
                for (var mt : course.meetingTimes()) {
                    if (queriedDays.contains(mt.day())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            if (!queriedMeetingTimes.isEmpty()) {
                boolean found = false;
                for (var mt : course.meetingTimes()) {
                    for (var qmt : queriedMeetingTimes) {
                        if (mt.hour() == qmt.hour() && mt.minute() == qmt.minute()) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            if (!queriedProfessors.isEmpty()) {
                boolean found = false;
                for (var pn : course.professorNames()) {
                    if (queriedProfessors.contains(pn)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
            }
            results.add(course);
        }

        currentQuery = query;
        // Trigger re-filtering
        filteredResults.clear();
        filteredResults.addAll(results);
        currentFilter = null;
    }
}
