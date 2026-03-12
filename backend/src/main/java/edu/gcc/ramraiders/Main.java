package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;


public class Main {

    private static final int PORT = 7000;

    public static CourseDB courseDB;

    public static void main(String[] args) throws IOException {

        courseDB = CourseDB.init();
        if (courseDB == null) {
            System.err.println("Failed to load course data");
            System.exit(-1);
        }

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors ->{
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });

        }).start(PORT);
        SearchController.registerRoutes(app);
    }
}