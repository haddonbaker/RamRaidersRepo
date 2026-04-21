package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.util.*;

public class SearchController {
    public static Search search;
    private static CourseDB courseDB;
    private static ProfessorDB professorDB;
    private static final Map<String, Schedule> schedules = new HashMap<>();

    private record SearchRequest(String query, Filter filter) { }
    private record AuthRequest(String username, String password) { }

    /**
     * Returns the schedule for a given term, scoped to a user when a username query param is present.
     * Key format: "username_Fall_2024" for logged-in users, "Fall_2024" for guests.
     * On first access for a logged-in user, loads their previously saved schedule from disk.
     */
    private static void persistIfLoggedIn(io.javalin.http.Context ctx, Schedule schedule) {
        String username = ctx.queryParam("username");
        if (username == null || username.isBlank()) return;
        String semester = ctx.queryParam("semester");
        String year = ctx.queryParam("year");
        if (semester == null) semester = "Fall";
        if (year == null) year = "2024";
        String termKey = semester + "_" + year;
        Student student = StudentDB.load(username);
        if (student != null) student.saveSchedule(termKey, schedule);
    }

    private static Schedule getSchedule(io.javalin.http.Context ctx) {
        String semester = ctx.queryParam("semester");
        String year = ctx.queryParam("year");
        String username = ctx.queryParam("username");
        if (semester == null) semester = "Fall";
        if (year == null) year = "2024";
        try { Course.SemesterType.valueOf(semester); }
        catch (IllegalArgumentException e) { semester = "Fall"; }

        String termKey = semester + "_" + year;
        boolean hasUser = username != null && !username.isBlank();
        String scheduleKey = hasUser ? username + "_" + termKey : termKey;

        final String finalTermKey = termKey;
        final String finalUsername = username;
        return schedules.computeIfAbsent(scheduleKey, k -> {
            if (finalUsername != null && !finalUsername.isBlank()) {
                Student student = StudentDB.load(finalUsername);
                if (student != null) {
                    Schedule saved = student.getSchedule(finalTermKey);
                    if (saved != null) return saved;
                }
            }
            return new Schedule();
        });
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

        app.get("/professorRatingGeneral", ctx -> {
            Main.log.info("get /professorRatingGeneral");
            ctx.json(SearchController.professorDB.getAllProfessors());
        });



        app.post("/addToCalendar", ctx -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            Main.log.info("post /addToCalendar");

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Course courseToAdd = mapper.convertValue(body.get("course"), Course.class);
            Schedule schedule = getSchedule(ctx);

            String result = schedule.add(courseToAdd);

            Main.log.error(schedule.getCourses().toString());

            switch (result) {
                case "SUCCESS":
                    persistIfLoggedIn(ctx, schedule);
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
            String username = ctx.queryParam("username");
            if (username == null || username.isBlank()) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: username required"));
                return;
            }
            Student student = StudentDB.load(username);
            if (student == null) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: unknown user"));
                return;
            }
            Main.log.info("post /saveSchedule : student " + username);
            // getSchedule uses the username query param to find the right in-memory schedule
            Schedule schedule = getSchedule(ctx);
            String semester = ctx.queryParam("semester");
            String year = ctx.queryParam("year");
            if (semester == null) semester = "Fall";
            if (year == null) year = "2024";
            String termKey = semester + "_" + year;
            boolean result = student.saveSchedule(termKey, schedule);
            if (result) {
                ctx.status(200).json(Map.of("status", "success", "message", "Schedule saved successfully"));
            } else {
                ctx.status(400).json(Map.of("status", "error", "message", "Failed to save schedule"));
            }
        });

        // --- Auth endpoints ---

        app.post("/createAccount", ctx -> {
            Main.log.info("post /createAccount");
            var req = ctx.bodyAsClass(AuthRequest.class);
            if (req.username() == null || req.username().isBlank()
                    || req.password() == null || req.password().isBlank()) {
                ctx.status(400).json(Map.of("status", "error", "message", "Username and password are required"));
                return;
            }
            Student student = Student.createAccount(req.username(), req.password());
            if (student == null) {
                ctx.status(409).json(Map.of("status", "error", "message", "Username already exists"));
                return;
            }
            ctx.status(201).json(Map.of("status", "success", "username", student.getUsername()));
        });

        app.post("/login", ctx -> {
            Main.log.info("post /login");
            var req = ctx.bodyAsClass(AuthRequest.class);
            if (req.username() == null || req.password() == null) {
                ctx.status(400).json(Map.of("status", "error", "message", "Username and password are required"));
                return;
            }
            Student student = Student.login(req.username(), req.password());
            if (student == null) {
                ctx.status(401).json(Map.of("status", "error", "message", "Invalid username or password"));
                return;
            }
            ctx.json(Map.of("status", "success", "username", student.getUsername(), "major", student.getMajor() != null ? student.getMajor() : ""));
        });

        app.patch("/updateMajor", ctx -> {
            String username = ctx.queryParam("username");
            if (username == null || username.isBlank()) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: username required"));
                return;
            }
            Student student = StudentDB.load(username);
            if (student == null) {
                ctx.status(404).json(Map.of("status", "error", "message", "Student not found"));
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String major = body.get("major");
            if (major == null || major.isBlank()) {
                ctx.status(400).json(Map.of("status", "error", "message", "major is required"));
                return;
            }
            Main.log.info("patch /updateMajor : student " + username + " -> " + major);
            if (student.saveMajor(major)) {
                ctx.json(Map.of("status", "success", "major", major));
            } else {
                ctx.status(500).json(Map.of("status", "error", "message", "Failed to save major"));
            }
        });

        app.patch("/updateDisplayName", ctx -> {
            Main.log.info("patch /updateDisplayName");
            String username = ctx.queryParam("username");
            if (username == null || username.isBlank()) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: username required"));
                return;
            }
            Student student = StudentDB.load(username);
            if (student == null) {
                ctx.status(404).json(Map.of("status", "error", "message", "Student not found"));
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String displayName = body.get("displayName");
            if (displayName == null || displayName.isBlank()) {
                ctx.status(400).json(Map.of("status", "error", "message", "Display name is required"));
                return;
            }
            Main.log.info("patch /updateDisplayName : student " + student.getDisplayName() + " -> " + displayName);
            if (student.saveDisplayName(displayName)) {
                ctx.json(Map.of("status", "success", "displayName", displayName));
            } else {
                ctx.status(500).json(Map.of("status", "error", "message", "Failed to save display name"));
            }
        });

        app.get("/getDisplayName", ctx -> {
            Main.log.info("get /getDisplayName");
            String username = ctx.queryParam("username");
            if (username == null || username.isBlank()) {
                ctx.status(401).json(Map.of("status", "error", "message", "Unauthorized: username required"));
                return;
            }
            Student student = StudentDB.load(username);
            if (student == null) {
                ctx.status(404).json(Map.of("status", "error", "message", "Student not found"));
                return;
            }
            ctx.json(Map.of("status", "success" , "displayName", student.getDisplayName()));
        });

        app.post("/logout", ctx -> {
            Main.log.info("post /logout");
            ctx.json(Map.of("status", "success", "message", "Logged out"));
        });

        // Checks whether a username still exists on disk (used to validate a cached login on page reload).
        app.get("/me", ctx -> {
            Main.log.info("get /me");
            String username = ctx.queryParam("username");
            if (username == null || username.isBlank() || !StudentDB.exists(username)) {
                ctx.status(401).json(Map.of("status", "error", "message", "Not logged in"));
                return;
            }
            Student student = StudentDB.load(username);
            String major = (student != null && student.getMajor() != null) ? student.getMajor() : "";
            ctx.json(Map.of("username", username, "major", major));
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
                persistIfLoggedIn(ctx, schedule);
                ctx.json(Map.of("status", "success", "schedule", schedule));
            } else {
                ctx.status(400).json(Map.of("status", "error", "message", "Course not found in schedule"));
            }
        });

        app.post("/undo", ctx->{
            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>)ctx.bodyAsClass(Map.class);
            Main.log.info("post /undo");
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var schedule = getSchedule(ctx);
            schedule.undo();
            persistIfLoggedIn(ctx, schedule);
            ctx.json(Map.of("status", "success", "schedule", schedule));
        });

        app.post("/redo", ctx->{
            @SuppressWarnings("unchecked")
            var body = (Map<String, Object>)ctx.bodyAsClass(Map.class);
            Main.log.info("post /undo");
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var schedule = getSchedule(ctx);
            schedule.redo();
            persistIfLoggedIn(ctx, schedule);
            ctx.json(Map.of("status", "success", "schedule", schedule));
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
