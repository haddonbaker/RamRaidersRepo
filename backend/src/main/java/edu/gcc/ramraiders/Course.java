package edu.gcc.ramraiders;

import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * Stores all information relating to a course
 *
 * @param department      The department name (COMP, ACCT, etc.)
 * @param code            The 3 digit course number aaa
 * @param section         The section type (A...Z|_) -- an underscore represents no section
 * @param name            Course name
 * @param semester        Semester
 * @param year            Year of the course
 * @param professorNames  names of professors teaching the course
 * @param meetingTimes    The weekly meeting times
 * @param openSeats       Number of open seats in a course
 * @param maxCapacity     Maximum number of seats in a course
 * @param referenceNumber The 5-digit identifier (TODO)
 * @param prerequisites   The prerequisites (TODO)
 * @param description     A more detailed description (TODO)
 */
public record Course(
        String department,
        Integer code,
        Character section,
        String name,
        SemesterType semester,
        Integer year,
        Set<String> professorNames,
        Set<MeetingTime> meetingTimes,
        Integer credits,
        Integer openSeats,
        Integer maxCapacity,
        Integer referenceNumber,
        Set<Course> prerequisites,
        String description
) {

    /**
     * Defines the possible semesters a course can take place
     */
    public enum SemesterType {
        Fall,
        WinterOnline,
        Spring,
        EarlySummer,
        LateSummer
    }

    /**
     * Defines the days a course can take place
     */
    public enum Day {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        None
    }

    /**
     * Defines the time of a course class meeting
     *
     * @param day         The day
     * @param hour        The hour
     * @param minute      The minute
     * @param minutesLong How many minutes long the meeting is
     */
    public record MeetingTime(Day day, int hour, int minute, int minutesLong) {
        public MeetingTime {
            Objects.requireNonNull(day);
        }
    }

    /**
     * Constructor
     */
    public Course {
        // This prints error messages if any of the course fields failed to parse.
        Objects.requireNonNull(department,
                "Invalid course department");
        Objects.requireNonNull(code,
                "Invalid course code");
        Objects.requireNonNull(section,
                String.format("Invalid course section for %s %d", department, code));
        Objects.requireNonNull(name,
                String.format("Invalid course name for %s %d %c", department, code, section));
        Objects.requireNonNull(semester,
                String.format("Invalid course semester for %s %d %c %s", department, code, section, name));
        Objects.requireNonNull(year,
                String.format("Invalid course year for %s %d %c %s", department, code, section, name));
        Objects.requireNonNull(professorNames,
                String.format("Invalid course professors for %s %d %c %s", department, code, section, name));
        Objects.requireNonNull(meetingTimes,
                String.format("Invalid course meeting for %s %d %c %s", department, code, section, name));
        Objects.requireNonNull(credits,
                String.format("Invalid credit hours for %s %d %c %s", department, code, section, name));
        Objects.requireNonNull(openSeats,
                String.format("Invalid course open seats for %s %d %c %s", department, code, section, name));
        Objects.requireNonNull(maxCapacity,
                String.format("Invalid course capacity for %s %d %c %s", department, code, section, name));
    }

    /**
     * @return A string representation of the course
     */
    @NotNull
    @Override
    public String toString() {
        return String.format("%s %d %s %s",
                Objects.requireNonNullElse(department, ""),
                code,
                Objects.requireNonNullElse(section, ' '),
                Objects.requireNonNullElse(name, ""));
    }

}