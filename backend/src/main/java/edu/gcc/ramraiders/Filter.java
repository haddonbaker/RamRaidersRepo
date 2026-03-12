package edu.gcc.ramraiders;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @param creditHours Selects a course if its number of credit hours is in this set. e.g: [3, 4, 5]
 * @param departments Selects a course if its department is in this set
 * @param professors Selects a Course if a string in this set is a substring of any course's professor names
 * @param isAvailable Selects a Course if it has available seats
 * @param timeslots Selects a Course if its meeting time fits into one of the timeslots
 * @param prerequisites Selects a Course if one of its prerequisites is in this set
 * @param semesters Selects a course if it's in a given semester
 * @param years Selects a course if it's in a given year
 * @param codes Selects a course if it has a code (e.g. 101 returns all 101 courses)
 *
 * @apiNote Any null value means that attribute is not being searched for
 */
public record Filter(
        Set<String> departments,
        Set<Integer> codes,
        Set<Course.SemesterType> semesters,
        Set<Integer> years,
        Set<String> professors,
        Set<Timeslot> timeslots,
        Set<Integer> creditHours,
        Boolean isAvailable,
        Set<Course> prerequisites
) {
    public Filter {
        if (departments == null) departments = Set.of();
        if (codes == null) codes = Set.of();
        if (semesters == null) semesters = Set.of();
        if (years == null) years = Set.of();
        if (professors == null) professors = Set.of();
        if (timeslots == null) timeslots = Set.of();
        if (creditHours == null) creditHours = Set.of();
        if (isAvailable == null) isAvailable = false;
        if (prerequisites == null) prerequisites = Set.of();
    }

    public record Timeslot(Course.Day day, int hour, int minute, int length) {}

    /**
     * @return A new Filter which is this filter combined with another filter.
     * @param other the other filter
     */
    public Filter combine(Filter other) {
        return new Filter(
                Stream.concat(departments.stream(), other.departments.stream()).collect(Collectors.toSet()),
                Stream.concat(codes.stream(), other.codes.stream()).collect(Collectors.toSet()),
                Stream.concat(semesters.stream(), other.semesters.stream()).collect(Collectors.toSet()),
                Stream.concat(years.stream(), other.years.stream()).collect(Collectors.toSet()),
                Stream.concat(professors.stream(), other.professors.stream()).collect(Collectors.toSet()),
                Stream.concat(timeslots.stream(), other.timeslots.stream()).collect(Collectors.toSet()),
                Stream.concat(creditHours.stream(), other.creditHours.stream()).collect(Collectors.toSet()),
                isAvailable || other.isAvailable,
                Stream.concat(prerequisites.stream(), other.prerequisites.stream()).collect(Collectors.toSet())
        );
    }
}
