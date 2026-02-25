package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
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