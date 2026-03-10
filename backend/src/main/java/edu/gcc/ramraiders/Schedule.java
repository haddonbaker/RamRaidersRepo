package edu.gcc.ramraiders;

import java.util.ArrayList;

public class Schedule {

    private ArrayList<Course> courses = new ArrayList<>();

    private int totalCredits;

    private ArrayList<String> errors = new ArrayList<>();


    /**
     *
     * @param c The course to be added
     * @return Returns 1 if successful and -1 if not --> Change to NULL
     */
    public int add (Course c ){
        //TODO: call checkForConflicts and then add a course object to the schedule or suggest other courses
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

        if (courses.contains(c)){
            //Probably need to check if removing this class goes below the min number of credits
            courses.remove(c);
        }else{
            //Throw an error, you can't remove a class you dont have
            //Stub print
            System.out.println("Course not found");
        }
        return -1;
    }

    public int save (Schedule c, Student s){
        //TODO: allow a user to save a schedule
        s.setMySchedule(c);
        return -1;
    }

    private boolean isConflict (Course c){
        //TODO: check the user's current candidate schedule for conflicts with adding the course c
        //Checks the seats
        if(c.openSeats() <= 0){
            return true;
        }
        //Checks for time overlaps
        //Also I don't think we need to check for semester because I am assuming the scheduling is all for the next semster, but I can add quickly if we need
        for (Course.MeetingTime mt1 : c.meetingTimes()) {
            for(int i = 0; i < courses.size(); i++) {
                Course a =  courses.get(i);
                for (Course.MeetingTime mt2 : a.meetingTimes()) {
                    //Checks if on the same day otherwise doesn't matter
                    if (mt1.day() == mt2.day()) {

                        int start1 = mt1.hour() * 60 + mt1.minute();
                        int end1 = start1 + mt1.minutesLong();

                        int start2 = mt2.hour() * 60 + mt2.minute();
                        int end2 = start2 + mt2.minutesLong();

                        // Overlap condition
                        if (start1 < end2 && start2 < end1) {
                            return true;
                        }
                    }
                }
            }
        }

        //Checks if exceeds max number of non extra payed credits
        if((getTotalCredits() + c.credits()) > 18){
            //Trigger warning for taking over 18 credits

            //This is part of the backlog
            System.out.println("Warning! Taking over 18 credits");
        }

        //Made it through all the checks must be correct
        return false;
    }

    private ArrayList<Course> suggestAlternatives(Course c){
        // TODO: take the course and schedule, find classes that are similar and do not conflict
        return new ArrayList<Course>();
    }

    public int getTotalCredits(){
        // TODO: return the private creditHours variable

        int totalCred = 0;
        for(int i = 0; i < courses.size(); i++){
            Course a = courses.get(i);
            int cred = a.credits();
            totalCred += cred;
        }
        totalCredits = totalCred;
        return totalCred;
    }


}
