package edu.gcc.ramraiders;

import io.javalin.Javalin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;


public class Main {

    private static final int PORT = 7000;

    public static final Log log = new Log(System.out);

    public static void main(String[] args) throws IOException {

        log.level = Log.LEVEL_DEBUG;
        log.info("Initialized log with level " + log.getLevelString());

        var courseDB = CourseDB.init();
        if (courseDB == null) {
            log.error("Failed to load course data");
            System.exit(-1);
        }
        log.info("Successfully loaded course data");

        Javalin app;
        try {
            app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(CorsPluginConfig.CorsRule::anyHost);
                });

            }).start(PORT);
        } catch (Exception e) {
            log.error("Error starting Javalin app: " + e);
            System.exit(-1);
            return;
        }
        log.info("Successfully started Javalin app");
        try {
            SearchController.registerRoutes(app, courseDB);
        } catch (Exception e) {
            log.error("Error registering routes: " + e);
            System.exit(-1);
        }
        log.info("Successfully registered search controller");
    }
}