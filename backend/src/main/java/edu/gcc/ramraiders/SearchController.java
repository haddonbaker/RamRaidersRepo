package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.util.*;
import java.util.stream.Collectors;

public class SearchController {
    public static void registerRoutes(Javalin app) {

        app.get("/courses", ctx -> {
            List<Course> allCourses = Objects.requireNonNull(CourseDB.init()).getCourseList();
            ctx.json(allCourses);
        });

        app.post("/results", ctx -> {
            // Parses the JSON body into Filter object
            Filter filter = ctx.bodyAsClass(Filter.class);
            Search search = new Search(Objects.requireNonNull(CourseDB.init()).getCourseList());
            search.applyFilter(filter);
            ctx.json(search.getCourses());
        });

        app.get("/fltr", ctx -> {
            Map<String, Object> filterOptions = new HashMap<>();
            List<Course> allCourses = Objects.requireNonNull(CourseDB.init()).getCourseList();

            // Departments: this on should get unique department codes
            filterOptions.put("departments", allCourses.stream()
                    .map(Course::department)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList()));

            // Meeting Times: It should extract unique time slots
            filterOptions.put("meetingTimes", allCourses.stream()
                    .flatMap(c -> c.meetingTimes().stream())
                    .distinct()
                    .sorted(Comparator.comparingInt(Course.MeetingTime::hour)
                            .thenComparingInt(Course.MeetingTime::minute))
                    .collect(Collectors.toList()));

            // Reference Numbers: ought to extract unique course reference numbers
            filterOptions.put("referenceNumbers", allCourses.stream()
                    .map(Course::referenceNumber)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList()));

            ctx.json(filterOptions);
        });

        app.get("/search", ctx -> {
            String query = ctx.queryParam("q");

            if (query == null || query.isEmpty()) {
                ctx.status(400).json(Map.of("status", "error", "message", "Query parameter 'q' is required"));
                return;
            }

            List<Course> results = Objects.requireNonNull(CourseDB.init()).getCourseList().stream()
                    .filter(c -> c.name().toLowerCase().contains(query.toLowerCase()) ||
                            c.department().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            ctx.json(results);
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
