package edu.gcc.ramraiders;

import java.util.ArrayList;

public class Schedule {

    private ArrayList<Course> courses = new ArrayList<>();

    private int creditHours;

    private ArrayList<String> errors = new ArrayList<>();

    public int add (Course c ){
        //TODO: call checkForConflicts and then add a course object to the schedule or suggest other courses
        return -1;
    }

    public int remove (Course c ){
        //TODO: remove a course object from the schedule
        return -1;
    }

    public int save (Schedule c, Student s){
        //TODO: allow a user to save a schedule
        return -1;
    }

    private int checkForConflicts (Course c, Schedule s){
        //TODO: check the user's current candidate schedule for conflicts with adding the course c
        return -1;
    }

    private ArrayList<Course> suggestAlternatives(Course c, Schedule s){
        // TODO: take the course and schedule, find classes that are similar and do not conflict
        return new ArrayList<Course>();
    }

    public int getCreditHours(Schedule s){
        // TODO: return the private creditHours variable
        return -1;
    }
}
