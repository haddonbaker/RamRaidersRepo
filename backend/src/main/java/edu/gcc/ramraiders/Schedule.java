package edu.gcc.ramraiders;

import java.util.ArrayList;

public class Schedule {

    private ArrayList<Course> courses = new ArrayList<>();

    private int creditHours;

    private ArrayList<String> errors = new ArrayList<>();

    public int add (Course c ){
        //TODO: call checkForConflicts and then add a course object to the schedule or suggest other courses


        //CHECK FOR CONFLICTS


        if(isConflict(c)){

            //PRINT SOME SORT OF ERROR MESSAGE
            //Need some sort of error to send back here, might turn into a try and catch block later
            suggestAlternatives(c);
            return -1; //FAIL CASE
        }else{
            //ADD THE COURSE TO THE SCHEDULE
            this.courses.add(c);
            return 1; //SUCCESS CASE

        }



    }

    public int remove (Course c ){
        //TODO: remove a course object from the schedule
        return -1;
    }

    public int save (Schedule c, Student s){
        //TODO: allow a user to save a schedule
        return -1;
    }

    private boolean isConflict (Course c){
        //TODO: check the user's current candidate schedule for conflicts with adding the course c
        //This should check the course against the current schedule.
        //***MIGHT NEED to take in the STUDENT object to check for classes***


        //capacity check
        if (c.openSeats() > 0){
            return true;
        }
        //PreReq Check
        /*if(c.getPrerequisites()){
            return true;
        }*/
        //Timing Check

        // FIXME: This needs to be rewritten, courses don't have one "start time" and "end time", they have a list of meeting times

//        int startTime = c.getStartTime();
//        int endTime = c.getEndTime();
//
//        for(int i = 0; i < courses.size(); i++){
//            if (courses.get(i).getStartTime() == startTime){
//                return true;
//                //Set some error
//            }
//            //There will be more logic with this it wont be finished yet
//        }
//





        //Default to true
        return true;
    }

    private ArrayList<Course> suggestAlternatives(Course c){
        // TODO: take the course and schedule, find classes that are similar and do not conflict
        return new ArrayList<Course>();
    }

    public int getCreditHours(Schedule s){
        // TODO: return the private creditHours variable
        return -1;
    }
}
