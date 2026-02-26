package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import io.javalin.http.staticfiles.Location;


public class Main {

    private static final int PORT = 7000;


    public static void main(String[] args) throws IOException {

        CourseDB courseDB = CourseDB.init();
        if (courseDB == null) {
            System.err.println("Failed to load course data");
            System.exit(-1);
        }

        System.out.println(courseDB.getCourseList().size());
        String workingDir = System.getProperty("user.dir");
        String frontendDir = Paths.get(workingDir, "..", "frontend", "frontend").normalize().toString();
        System.out.println("Frontend directory: " + frontendDir);

        Javalin.create(config -> {
            config.staticFiles.add(frontendDir, Location.EXTERNAL);
        }).start(PORT);

        // TODO: Endpoints
    }
}