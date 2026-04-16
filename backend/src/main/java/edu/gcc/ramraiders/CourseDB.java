package edu.gcc.ramraiders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Stores and allows querying of all information related to courses.
 */
public class CourseDB {

    private static CourseDB INSTANCE = null;
    private final List<Course> courseList = new ArrayList<>();


    private final Map<String, Set<Course>> departmentToCourse = new HashMap<>();
    private final Map<Integer, Set<Course>> codeToCourse = new HashMap<>();
    private final Map<Course.SemesterType, Set<Course>> semesterToCourse = new HashMap<>();
    private final Map<Integer, Set<Course>> yearToCourse = new HashMap<>();
    private final Map<Integer, Set<Course>> creditsToCourse = new HashMap<>();

    private final Set<String> possibleDepartments = new TreeSet<>();
    private final Set<Integer> possibleCredits = new TreeSet<>();
    private final Set<Integer> possibleYears = new TreeSet<>();
    private final Set<String> possibleProfessors = new TreeSet<>();
    private final Set<String> possibleCourseNames = new TreeSet<>();

    /**
     * Loads the course data and returns an instance of CourseDB. This can only be done once.
     *
     * @return An instance of CourseDB, null if failed to load.
     */
    public static CourseDB init() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new CourseDB();
                return INSTANCE;
            } catch (IOException e) {
                return null;
            }
        } else {
            System.err.println("Course DB already initialized");
            return null;
        }
    }

    /**
     * Constructor
     *
     * @throws IOException if the course data cannot be loaded
     */
    private CourseDB() throws IOException {
        var mapper = new ObjectMapper();
        var data = mapper.readValue(Main.class.getResourceAsStream("/course_data.json"), Map.class);
        var classes = (ArrayList<?>) data.get("classes");

        for (var c : classes) {
            var course = parseCourse((Map<?, ?>) c);
            if (course != null) {
                courseList.add(course);
            }
        }
        // Populate the lookup tables
        for (var course : courseList) {
            departmentToCourse.putIfAbsent(course.department(), new HashSet<>());
            departmentToCourse.get(course.department()).add(course);
            possibleDepartments.add(course.department());

            codeToCourse.putIfAbsent(course.code(), new HashSet<>());
            codeToCourse.get(course.code()).add(course);

            semesterToCourse.putIfAbsent(course.semester(), new HashSet<>());
            semesterToCourse.get(course.semester()).add(course);

            yearToCourse.putIfAbsent(course.year(), new HashSet<>());
            yearToCourse.get(course.year()).add(course);
            possibleYears.add(course.year());

            creditsToCourse.putIfAbsent(course.credits(), new HashSet<>());
            creditsToCourse.get(course.credits()).add(course);
            possibleCredits.add(course.credits());

            possibleProfessors.addAll(course.professorNames());
            possibleCourseNames.add(course.name());
        }
    }

    /**
     * @return The set of possible departments in the course collection
     */
    public Set<String> getPossibleDepartments() {
        return possibleDepartments;
    }

    /**
     * @return The set of possible numbers of credits in the course selection
     */
    public Set<Integer> getPossibleCredits() {
        return possibleCredits;
    }

    /**
     * @return The set of possible years in the course selection
     */
    public Set<Integer> getPossibleYears() {
        return possibleYears;
    }

    /**
     * @return The set of possible professors in the course selection
     */
    public Set<String> getPossibleProfessors() {
        return possibleProfessors;
    }

    /**
     * @return The set of possible course names in the course selection
     */
    public Set<String> getPossibleCourseNames() {
        return possibleCourseNames;
    }

    /**
     * @param department The department
     * @return The set of courses under the specific department
     */
    public Set<Course> getCoursesByDepartment(String department) {
        return departmentToCourse.getOrDefault(department, new HashSet<>());
    }

    /**
     * @param code The code
     * @return The set of courses with a specific code
     */
    public Set<Course> getCoursesByCode(int code) {
        return codeToCourse.getOrDefault(code, new HashSet<>());
    }

    /**
     * @param semesterType The semester
     * @return The set of courses in a specific semester
     */
    public Set<Course> getCoursesBySemester(Course.SemesterType semesterType) {
        return semesterToCourse.getOrDefault(semesterType, new HashSet<>());
    }

    /**
     * @param year The year
     * @return The set of courses in a specific year
     */
    public Set<Course> getCoursesByYear(int year) {
        return yearToCourse.getOrDefault(year, new HashSet<>());
    }

    /**
     * @param credits The credits
     * @return The set of courses with a specific amount of credits
     */
    public Set<Course> getCoursesByCredits(int credits) {
        return creditsToCourse.getOrDefault(credits, new HashSet<>());
    }

    /**
     * @return The list of courses
     */
    public List<Course> getCourseList() {
        return courseList;
    }

    /**
     * Returns only semester+year combinations that actually have courses, sorted chronologically
     * (ascending by year, then by semester order within the year).
     *
     * @return Sorted list of term strings in "Semester_Year" format, e.g. "Fall_2024"
     */
    public List<String> getTerms() {
        List<String> semesterOrder = Arrays.stream(Course.SemesterType.values()).map(Enum::name).toList();
        return courseList.stream()
            .map(c -> c.semester().name() + "_" + c.year())
            .distinct()
            .sorted(Comparator
                .comparingInt((String t) -> Integer.parseInt(t.split("_")[1]))
                .thenComparingInt(t -> semesterOrder.indexOf(t.split("_")[0])))
            .toList();
    }


    // Helper functions

    /**
     * Creates a course record from a JSON object.
     *
     * @param obj the object
     * @return a course record
     */
    private static Course parseCourse(Map<?, ?> obj) {
        try {
            return new Course(
                    (String) obj.get("department") + obj.get("code") +"-"+obj.get("section"),
                    (String) obj.get("subject"),
                    (Integer) obj.get("number"),
                    parseSection(obj),
                    (String) obj.get("name"),
                    parseSemester(obj),
                    parseYear(obj),
                    parseProfessorNames(obj),
                    parseMeetingTimes(obj),
                    (Integer) obj.get("credits"),
                    (Integer) obj.get("open_seats"),
                    (Integer) obj.get("total_seats"),
                    0,
                    new HashSet<>(),
                    ""
            );
        } catch (ClassCastException | NullPointerException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Parse the course section from a JSON sub object. The section is an optional attribute.
     *
     * @param m the object
     * @return the section
     */
    private static Character parseSection(Map<?, ?> m) {
        try {
            return m.containsKey("section") ? ((String) m.get("section")).charAt(0) : '_';
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse the course year from a JSON sub object
     *
     * @param m the object
     * @return the year
     */
    private static Integer parseYear(Map<?, ?> m) {
        try {
            return Integer.parseInt(((String) m.get("semester")).split("_")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse the semester type from a JSON sub object
     *
     * @param m the object
     * @return the semester type
     */
    private static Course.SemesterType parseSemester(Map<?, ?> m) {
        try {
            final var t = ((String) m.get("semester")).split("_");
            return switch (t[1]) {
                case "Fall" -> Course.SemesterType.Fall;
                case "Spring" -> Course.SemesterType.Spring;
                case "Winter" -> Course.SemesterType.WinterOnline;
                case "Early" -> t[2].equals("Summer") ? Course.SemesterType.EarlySummer : null;
                case "Late" -> t[2].equals("Summer") ? Course.SemesterType.LateSummer : null;
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse the list of professors from a JSON sub object
     *
     * @param m The object
     * @return The set of professor names, null if any error occurred in parsing
     */
    private static Set<String> parseProfessorNames(Map<?, ?> m) {
        try {
            if (!(m.get("faculty") instanceof ArrayList<?> professorList)) {
                return null;
            }
            var professorNames = new HashSet<String>();
            for (var prof : professorList) {
                if (prof instanceof String str) {
                    professorNames.add(str);
                } else {
                    return null;
                }
            }
            return professorNames;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse the meeting times from a JSON sub object
     *
     * @param m The object
     * @return The list of meeting times, null if any error occurred in parsing
     */
    private static Set<Course.MeetingTime> parseMeetingTimes(Map<?, ?> m) {
        try {
            var meetingTimes = new HashSet<Course.MeetingTime>();
            var timesObj = m.get("times");
            if (!(timesObj instanceof ArrayList<?>)) {
                return null;
            }
            for (var timeObj : (ArrayList<?>) timesObj) {
                if (!(timeObj instanceof Map<?, ?> time)) {
                    return null;
                }
                var dayObj = time.get("day");
                var endTimeObj = time.get("end_time");
                var startTimeObj = time.get("start_time");
                String dayStr;
                String endTimeStr;
                String startTimeStr;
                if (dayObj instanceof String && endTimeObj instanceof String && startTimeObj instanceof String) {
                    dayStr = (String) dayObj;
                    endTimeStr = (String) endTimeObj;
                    startTimeStr = (String) startTimeObj;
                } else {
                    return null;
                }
                var st = LocalDateTime.parse("2000-01-01T" + startTimeStr);
                var et = LocalDateTime.parse("2000-01-01T" + endTimeStr);
                int hour = st.getHour();
                int minute = st.getMinute();
                int minutesLong = (et.toLocalTime().toSecondOfDay() - st.toLocalTime().toSecondOfDay()) / 60;
                meetingTimes.add(new Course.MeetingTime(strToDay.get(dayStr), hour, minute, minutesLong));
            }
            return meetingTimes;
        } catch (Exception e) {
            return null;
        }
    }

    private final static Map<String, Course.Day> strToDay = Map.of(
            "M", Course.Day.Monday,
            "T", Course.Day.Tuesday,
            "W", Course.Day.Wednesday,
            "R", Course.Day.Thursday,
            "F", Course.Day.Friday);
}
