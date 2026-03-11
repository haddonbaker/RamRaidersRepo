package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.util.*;
import java.util.stream.Collectors;

public class SearchController {
    private static Search search;
    private record SearchRequest(String query, Filter filter) { }


    public static void registerRoutes(Javalin app) {
        search = new Search();

        app.get("/courses", ctx -> {
            List<Course> allCourses = Main.courseDB.getCourseList();
            ctx.json(allCourses);
        });

        app.post("/search", ctx -> {
            // No error handling necessary: malformed JSON automatically returns a 400 error code with Javalin
            var request = ctx.bodyAsClass(SearchRequest.class);
            ctx.json(search.search(request.query, request.filter));
        });

        app.post("/calendar", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);  // ✅ Read once

            //from what I saw this can turn maps into objects
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();


            Schedule schedule = mapper.convertValue(body.get("schedule"), Schedule.class);
            Course courseToAdd = mapper.convertValue(body.get("course"), Course.class);

            int result = schedule.add(courseToAdd);
            if (result == 1) {
                ctx.json(Map.of("status", "success", "schedule", schedule));
            } else {
                ctx.status(400).json(Map.of("status", "error", "message", "Conflict or full capacity"));
            }
        });

        app.post("/saveSchedule", ctx -> {
            Student student = ctx.sessionAttribute("student");
            Schedule schedule = ctx.bodyAsClass(Schedule.class);

            if(student == null) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: Please log in"));
                return;
            }
            int result = schedule.save(schedule, student);

            if (result == 1) {
                ctx.status(200).json(java.util.Map.of("status", "success", "message", "Schedule saved successfully"));
            } else {
                ctx.status(400).json(java.util.Map.of("status", "error", "message", "Failed to save schedule"));
            }
        });

    }
}
