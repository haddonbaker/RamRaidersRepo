package edu.gcc.ramraiders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Schedule {

    private ArrayList<Course> courses = new ArrayList<>();

    private int totalCredits;

    private ArrayList<String> errors = new ArrayList<>();


    /**
     *
     * @param c The course to be added
     * @return Returns string with success or conflict type -  SUCCESS | duplicate | full | overlap
     */
    public String add (Course c ){
        String result = ConflictType(c);
        if(!result.equals("none")){
            //TODO(Blanks): ADD ERROR HERE
            return result; //FAIL
        }else{
            courses.add(c);
            return "SUCCESS"; //SUCCESS

        }
    }

    public boolean isDuplicate(Course c){
        for(Course course: courses){
            if(course.getId().equals(c.getId())){
                return true; //Already have courses
            }
            String parseId = course.department() + course.code();
            if((course.department() + course.code()).equals(c.department()+c.code())){

                boolean courseIsLab = course.section() == 'L';
                boolean cIsLab = c.section() == 'L';

                // block if both are lectures or both labs
                if(!courseIsLab && !cIsLab){
                    return true;
                }
                //We might want a different return message here but it works for rn
            }

        }
        return false;
    }

    public int remove (Course c ){

        for(Course course: courses){
            if(course.getId().equals(c.getId())){
                courses.remove(course);
                return 1; //SUCCESS
            }
        }
        //TODO(Blanks): ADD ERROR HERE
        return -1; //FAIL
    }

    public int save (Schedule c, Student s){
        // Associate schedule with student in memory

        Path dir = Paths.get("schedules");
        try {
            Files.createDirectories(dir);

            int idx = 1;
            Path filePath = null;
            while (idx < Integer.MAX_VALUE) {
                Path candidate = dir.resolve("schedule" + idx + ".txt");
                try {
                    // fails if the file exists
                    filePath = Files.createFile(candidate);
                    break;
                } catch (FileAlreadyExistsException ex) {
                    idx++;
                }
            }

            if (filePath == null) {
                return -1;
            }

            try (BufferedWriter w = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                // puts the studnt name at the top
                String username = (s.getUsername() != null) ? s.getUsername() : "Unknown";
                w.write("Student: " + username);
                w.newLine();
                w.newLine();

                // Schedule contents
                w.write("Schedule:");
                w.newLine();

                if (c != null && c.getCourses() != null && !c.getCourses().isEmpty()) {
                    for (Course cr : c.getCourses()) {
                        w.write(cr.toString());
                        w.newLine();
                    }
                } else {
                    w.write("(no courses)");
                    w.newLine();
                }
            }

            // a great success!
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String ConflictType (Course c){
        //TODO: check the user's current candidate schedule for conflicts with adding the course c
        //Checks the seats
        if(c.openSeats() <= 0){
            return "full";
        }

        if (isDuplicate(c)){
            return "duplicate";
        }
        //Checks for time overlaps
        //Also I don't think we need to check for semester because I am assuming the scheduling is all for the next semster, but I can add quickly if we need
        for (Course.MeetingTime mt1 : c.meetingTimes()) {
            for (int i = 0; i < courses.size(); i++) {
                Course a = courses.get(i);
                for (Course.MeetingTime mt2 : a.meetingTimes()) {
                    //Checks if on the same day otherwise doesn't matter
                    if (mt1.day() == mt2.day()) {

                        int start1 = mt1.hour() * 60 + mt1.minute();
                        int end1 = start1 + mt1.minutesLong();

                        int start2 = mt2.hour() * 60 + mt2.minute();
                        int end2 = start2 + mt2.minutesLong();

                        // Overlap condition
                        if (start1 < end2 && start2 < end1) {
                            return "overlap";
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
        return "none";
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

    public ArrayList<Course> getCourses() {
        return courses;
    }


}
