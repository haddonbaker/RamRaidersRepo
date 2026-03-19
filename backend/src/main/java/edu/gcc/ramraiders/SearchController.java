package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.util.*;

public class SearchController {
    public static Search search;
    private static CourseDB courseDB;
    private static final Schedule globalSchedule = new Schedule();

    private record SearchRequest(String query, Filter filter) { }


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

        app.get("/years", ctx -> ctx.json(SearchController.courseDB.getPossibleYears()));

        app.get("/departments", ctx -> ctx.json(SearchController.courseDB.getPossibleDepartments()));

        app.get("/credits", ctx -> ctx.json(SearchController.courseDB.getPossibleCredits()));

        app.get("/professors", ctx-> ctx.json(SearchController.courseDB.getPossibleProfessors()));

        app.post("/addToCalendar", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            //from what I saw this can turn maps into objects
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();


            // Schedule schedule = mapper.convertValue(body.get("schedule"), Schedule.class); // Use global schedule instead
            Course courseToAdd = mapper.convertValue(body.get("course"), Course.class);

            String result = globalSchedule.add(courseToAdd);

            switch (result) {
                case "SUCCESS":
                    ctx.json(Map.of("status", "success", "schedule", globalSchedule));
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
            int result = globalSchedule.save(globalSchedule, student);

            if (result == 1) {
                ctx.status(200).json(java.util.Map.of("status", "success", "message", "Schedule saved successfully"));
            } else {
                ctx.status(400).json(java.util.Map.of("status", "error", "message", "Failed to save schedule"));
            }
        });

        app.delete("/removeFromCalendar", ctx -> { // This is where the user was editing
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            // Schedule schedule = mapper.convertValue(body.get("schedule"), Schedule.class); // Use global schedule
            Course courseToRemove = mapper.convertValue(body.get("course"), Course.class);

            int result = globalSchedule.remove(courseToRemove);
            if (result == 1) {
                ctx.json(Map.of("status", "success", "schedule", globalSchedule));
            } else {
                ctx.status(400).json(Map.of("status", "error", "message", "Course not found in schedule"));
            }
        });

        app.get("/schedule", ctx -> ctx.json(globalSchedule));

        app.post("/suggestAlternatives", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Course course = mapper.convertValue(body.get("course"), Course.class);
            
            // Use globalSchedule instead of parsing from body
            Set<Course> alternatives = course.SuggestAlternatives(course, globalSchedule);
            ctx.json(alternatives);
        });
    }
}