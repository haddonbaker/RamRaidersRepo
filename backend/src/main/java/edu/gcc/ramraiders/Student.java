package edu.gcc.ramraiders;

public class Student {
    private String username;

    private String password;

    private Schedule mySchedule; // the schedule associated with this user

    private String advisorEmail; // the user's advisor

    private String statusSheet; // the name of the student's status sheet, e.g. COMP2025.txt for a Computer Science major following the 2025 status sheet

    private String[] major; // the user's declared major(s)

    private String[] minor; // the user's declared minor(s)

    public int login(String username, String password){
        //TODO: check the user's credentials against the accounts that exist already
        return -1;
    }

    private int logout(){
        //TODO: clear username and password from variables
        return -1;
    }
}
