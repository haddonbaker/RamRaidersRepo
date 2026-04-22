package edu.gcc.ramraiders;

import java.util.HashMap;
import java.util.Map;

public class ScheduleService {

    // In-memory schedule cache; key format: "username_Fall_2024" (logged-in) or "Fall_2024" (guest)
    private static final Map<String, Schedule> schedules = new HashMap<>();

    public static Schedule getSchedule(String username, String semester, String year) {
        String termKey = buildTermKey(semester, year);
        boolean hasUser = username != null && !username.isBlank();
        String scheduleKey = hasUser ? username + "_" + termKey : termKey;

        return schedules.computeIfAbsent(scheduleKey, k -> {
            if (hasUser) {
                Student student = StudentDB.load(username);
                if (student != null) {
                    Schedule saved = student.getSchedule(termKey);
                    if (saved != null) return saved;
                }
            }
            return new Schedule();
        });
    }

    public static void persistIfLoggedIn(String username, String semester, String year, Schedule schedule) {
        if (username == null || username.isBlank()) return;
        String termKey = buildTermKey(semester, year);
        Student student = StudentDB.load(username);
        if (student != null) student.saveSchedule(termKey, schedule);
    }

    public static String buildTermKey(String semester, String year) {
        if (semester == null) semester = "Fall";
        if (year == null) year = "2024";
        try { Course.SemesterType.valueOf(semester); }
        catch (IllegalArgumentException e) { semester = "Fall"; }
        return semester + "_" + year;
    }
}
