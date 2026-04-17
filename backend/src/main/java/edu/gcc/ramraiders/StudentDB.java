package edu.gcc.ramraiders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages loading and saving Student records as JSON files in the local "students/" directory.
 * One file per student: students/{username}.json
 */
public class StudentDB {

    private static final Path DIR = Paths.get("students");
    private static final ObjectMapper mapper = new ObjectMapper();

    /** Loads a student by username. Returns null if not found or on error. */
    public static Student load(String username) {
        Path file = DIR.resolve(sanitize(username) + ".json");
        if (!Files.exists(file)) return null;
        try {
            return mapper.readValue(file.toFile(), Student.class);
        } catch (Exception e) {
            Main.log.error("Failed to load student '" + username + "': " + e);
            return null;
        }
    }

    /** Saves a student to disk. Returns true on success. */
    public static boolean save(Student student) {
        try {
            Files.createDirectories(DIR);
            Path file = DIR.resolve(sanitize(student.getUsername()) + ".json");
            mapper.writeValue(file.toFile(), student);
            return true;
        } catch (Exception e) {
            Main.log.error("Failed to save student '" + student.getUsername() + "': " + e);
            return false;
        }
    }

    /** Returns true if a student file exists for the given username. */
    public static boolean exists(String username) {
        return Files.exists(DIR.resolve(sanitize(username) + ".json"));
    }

    /** Strips characters that would be unsafe in a filename to prevent path traversal. */
    private static String sanitize(String username) {
        return username.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
