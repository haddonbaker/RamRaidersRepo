package edu.gcc.ramraiders;

public class Student {
    private String username;

    private String password;

    private Schedule mySchedule; // the schedule associated with this user

    private String advisorEmail; // the user's advisor

    private String statusSheet; // the name of the student's status sheet, e.g. COMP2025.txt for a Computer Science major following the 2025 status sheet

    private String[] major; // the user's declared major(s)

    private String[] minor; // the user's declared minor(s)

    public Student(){

    }
    public Student (String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setMySchedule(Schedule s) {
        this.mySchedule = s;

    }


    public int login(String username, String password){
        //TODO: check the user's credentials against the accounts that exist already
        Main.log.info("Student " + username + " logged in successfully");
        return -1;
    }

    private int logout(){
        //TODO: clear username and password from variables
        Main.log.info("Student " + username + " logged out successfully");
        return -1;
    }

    public String getUsername() {
        return username;
    }
}
