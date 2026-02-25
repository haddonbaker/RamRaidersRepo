package edu.gcc.ramraiders;

import java.util.Set;

public class Course {

    public enum SectionType {
        A, B, C, D, E, F, G
    }

    public enum SemesterType {
        Fall,
        WinterOnline,
        Spring,
        EarlySummer,
        LateSummer
    }

    public enum Day {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday
    }

    private int courseCode; // 3 digit course number

    private int referenceNumber; // 5 digit unique course specifier

    private int creditHours; // weekly in-class hours

    private String professorName; // the person teaching the course

    private String courseDescription; // for using in keyword search

    private String courseName; // the name of the course

    private SectionType section; // A/B/C/D/E/F/G depending on how many sections the course has

    private SemesterType semester; // Fall / Spring (add year?)

    private Set<Day> days; // set of enumerated type days for what days the class meets, e.g. MTWRF

    private String department; // the department the class lives under

    private int numEnrolled; // number of students enrolled in the class

    private int maxCapacity; // maximum number of seats in a class

    private int capacity;

    private Course[] prerequisites; // an array of prerequisite courses that must be taken before this class

    public int getCourseCode() {
        return courseCode;
    }
    public int getReferenceNumber(){
        return referenceNumber;
    }
    public int getCreditHours(){
        return creditHours;
    }
    public String getProfessorName(){
        return professorName;
    }
    public String getCourseDescription(){
        return courseDescription;
    }
    public String getCourseName(){
        return courseName;
    }
    public SectionType getSection(){
        return section;
    }
    public SemesterType getSemester(){
        return semester;
    }
    public Set<Day> getDays(){
        return days;
    }
    public String getDepartment(){
        return department;
    }
    public int getNumEnrolled(){
        return numEnrolled;
    }
    public int getMaxCapacity(){
        return maxCapacity;
    }
    public int getCapacity(){
        return capacity;
    }
    public Course[] getPrerequisites(){
        return prerequisites;
    }




}
