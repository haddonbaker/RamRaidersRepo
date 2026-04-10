package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.util.*;

public class SearchController {
    public static Search search;
    private static CourseDB courseDB;
    private static final Map<String, Schedule> schedules = new HashMap<>();

    private record SearchRequest(String query, Filter filter) { }

    /** Returns a schedule key like "Fall_2024". Creates the schedule lazily if it doesn't exist. */
    private static Schedule getSchedule(io.javalin.http.Context ctx) {
        String semester = ctx.queryParam("semester");
        String year = ctx.queryParam("year");
        if (semester == null) semester = "Fall";
        if (year == null) year = "2024";
        try { Course.SemesterType.valueOf(semester); }
        catch (IllegalArgumentException e) { semester = "Fall"; }
        return schedules.computeIfAbsent(semester + "_" + year, k -> new Schedule());
    }

    public static void registerRoutes(Javalin app, CourseDB courseDB) {
        search = new Search(courseDB);
        SearchController.courseDB = courseDB;

        app.get("/courses", ctx -> {
            List<Course> allCourses = courseDB.getCourseList();
            ctx.json(allCourses);
        });

        app.post("/search", ctx -> {
            // No error handling necessary: malformed JSON automatically returns a 400 error code with Javalin
            var request = ctx.bodyAsClass(SearchRequest.class);
            ctx.json(search.search(request.query, request.filter));
        });

        app.get("/semesters", ctx -> ctx.json(
            Arrays.stream(Course.SemesterType.values()).map(Enum::name).toList()
        ));

        app.get("/years", ctx -> ctx.json(SearchController.courseDB.getPossibleYears()));

        // Returns only semester+year combinations that actually have courses, sorted chronologically.
        app.get("/terms", ctx -> {
            List<String> semesterOrder = Arrays.stream(Course.SemesterType.values()).map(Enum::name).toList();
            List<String> terms = courseDB.getCourseList().stream()
                .map(c -> c.semester().name() + "_" + c.year())
                .distinct()
                .sorted(Comparator
                    .comparingInt((String t) -> Integer.parseInt(t.split("_")[1]))
                    .thenComparingInt(t -> semesterOrder.indexOf(t.split("_")[0])))
                .toList();
            ctx.json(terms);
        });

        app.get("/departments", ctx -> ctx.json(SearchController.courseDB.getPossibleDepartments()));

        app.get("/credits", ctx -> ctx.json(SearchController.courseDB.getPossibleCredits()));

        app.get("/professors", ctx-> ctx.json(SearchController.courseDB.getPossibleProfessors()));

        app.post("/addToCalendar", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Course courseToAdd = mapper.convertValue(body.get("course"), Course.class);
            Schedule schedule = getSchedule(ctx);

            String result = schedule.add(courseToAdd);

            switch (result) {
                case "SUCCESS":
                    ctx.json(Map.of("status", "success", "schedule", schedule));
                    break;
                case "duplicate":
                    ctx.status(400).json(Map.of("status", "error", "message", "Course is already in the schedule"));
                    break;
                case "full":
                    ctx.status(400).json(Map.of("status", "error", "message", "Course is already at full capacity"));
                    break;
                case "overlap":
                    ctx.status(400).json(Map.of("status", "error", "message", "Course overlaps with another scheduled course"));
                    break;
                default:
                    ctx.status(400).json(Map.of("status", "error", "message", "Unknown error occurred"
                    ));
            }
        });

        app.post("/saveSchedule", ctx -> {
            //Student student = ctx.sessionAttribute("student");
            //Schedule schedule = ctx.bodyAsClass(Schedule.class);
            Student student = new Student("test","12345");
            if(student == null) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: Please log in"));
                return;
            }
            Schedule schedule = getSchedule(ctx);
            int result = schedule.save(schedule, student);

            if (result == 1) {
                ctx.status(200).json(java.util.Map.of("status", "success", "message", "Schedule saved successfully"));
            } else {
                ctx.status(400).json(java.util.Map.of("status", "error", "message", "Failed to save schedule"));
            }
        });

        app.delete("/removeFromCalendar", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Course courseToRemove = mapper.convertValue(body.get("course"), Course.class);
            Schedule schedule = getSchedule(ctx);

            int result = schedule.remove(courseToRemove);
            if (result == 1) {
                ctx.json(Map.of("status", "success", "schedule", schedule));
            } else {
                ctx.status(400).json(Map.of("status", "error", "message", "Course not found in schedule"));
            }
        });

        app.get("/schedule", ctx -> ctx.json(getSchedule(ctx)));

        app.post("/suggestAlternatives", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Course course = mapper.convertValue(body.get("course"), Course.class);
            Schedule schedule = getSchedule(ctx);
            Set<Course> alternatives = course.SuggestAlternatives(course, schedule);
            ctx.json(alternatives);
        });
    }
}
