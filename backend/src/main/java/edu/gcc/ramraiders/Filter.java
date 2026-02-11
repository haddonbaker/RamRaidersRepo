package edu.gcc.ramraiders;
import java.util.Set;
import java.util.Date;

public class Filter {

    /// Selects a course if its number of credit hours is in this set
    private Set<Integer> creditHours;

    /// Selects a course if its department is in this set
    private Set<String> departments;

    /// Selects a Course if its professors is in this set
    private Set<String> professors;

    /// Selects a Course if it has a minimum number of available slots
    private int availableSlots;

    /// Selects a Course if its meeting time is in this set
    private Set<Date> times;

    /// Selects a Course if one of its prerequisites is in this set
    private Set<Course> prerequisites;
}
