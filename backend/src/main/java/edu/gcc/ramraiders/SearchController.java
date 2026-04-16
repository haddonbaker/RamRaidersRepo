package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.util.*;

public class SearchController {
    public static Search search;
    private static CourseDB courseDB;
    private static ProfessorDB professorDB;
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

    public static void registerRoutes(Javalin app, CourseDB courseDB, ProfessorDB professorDB) {
        search = new Search(courseDB);
        SearchController.courseDB = courseDB;
        SearchController.professorDB = professorDB;

        app.get("/courses", ctx -> {
            Main.log.info("get /courses");
            List<Course> allCourses = courseDB.getCourseList();
            ctx.json(allCourses);
        });

        app.post("/search", ctx -> {
            Main.log.info("post /search");
            // No error handling necessary: malformed JSON automatically returns a 400 error code with Javalin
            var request = ctx.bodyAsClass(SearchRequest.class);
            ctx.json(search.search(request.query, request.filter));
        });

        app.get("/semesters", ctx -> {
            Main.log.info("get /semesters");
            ctx.json(Arrays.stream(Course.SemesterType.values()).map(Enum::name).toList());
        });

        app.get("/years", ctx -> {
            Main.log.info("get /years");
            ctx.json(SearchController.courseDB.getPossibleYears());
        });

        // Returns only semester+year combinations that actually have courses, sorted chronologically.
        app.get("/terms", ctx -> {
            Main.log.info("get /terms");
            ctx.json(SearchController.courseDB.getTerms());
        });

        app.get("/departments", ctx -> {
            Main.log.info("get /departments");
            ctx.json(SearchController.courseDB.getPossibleDepartments());
        });

        app.get("/credits", ctx -> {
            Main.log.info("get /credits");
            ctx.json(SearchController.courseDB.getPossibleCredits());
        });

        app.get("/professors", ctx-> {
            Main.log.info("get /professors");
            ctx.json(SearchController.courseDB.getPossibleProfessors());
        });

        // Accepts a faculty name in course_data format ("Last, First M.") as the `name`
        // query param and returns the matching RateMyProfessors entry, or 404 if not found.
        app.get("/professorRating", ctx -> {
            String name = ctx.queryParam("name");
            Main.log.info("get /professorRating name=" + name);
            if (name == null || name.isBlank()) {
                ctx.status(400).json(Map.of("status", "error", "message", "Missing required query param: name"));
                return;
            }
            var professor = SearchController.professorDB.findByCourseDataName(name);
            if (professor == null) {
                ctx.status(404).json(Map.of("status", "error", "message", "No rating found for: " + name));
                return;
            }
            ctx.json(professor);
        });

        app.post("/addToCalendar", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            Main.log.info("post /addToCalendar");

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
            Main.log.info("post /saveSchedule : student " + student.getUsername());
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

            Main.log.info("delete /removeFromCalendar");

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

        app.get("/schedule", ctx -> {
            Main.log.info("get /schedule");
            ctx.json(getSchedule(ctx));
        });

        app.post("/suggestAlternatives", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            Main.log.info("post /suggestAlternatives");

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Course course = mapper.convertValue(body.get("course"), Course.class);
            Schedule schedule = getSchedule(ctx);
            Set<Course> alternatives = course.SuggestAlternatives(course, schedule);
            ctx.json(alternatives);
        });
    }
}
