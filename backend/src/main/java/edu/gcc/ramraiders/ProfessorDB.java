package edu.gcc.ramraiders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * Loads professors.json (RateMyProfessors data) and matches entries against
 * faculty names from course_data.json.
 *
 * <p>course_data uses "Last, First M." format; professors.json uses "First Last"
 * format. Matching is done by comparing last name and first name
 * case-insensitively, ignoring middle initials.</p>
 */
public class ProfessorDB {

    private static ProfessorDB INSTANCE = null;

    /** Raw professor objects keyed by normalized "firstname lastname". */
    private final Map<String, Map<String, Object>> byNormalizedName = new HashMap<>();

    public static ProfessorDB init() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new ProfessorDB();
                return INSTANCE;
            } catch (IOException e) {
                return null;
            }
        } else {
            System.err.println("ProfessorDB already initialized");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private ProfessorDB() throws IOException {
        var mapper = new ObjectMapper();
        var professors = mapper.readValue(
                Main.class.getResourceAsStream("/professors.json"),
                List.class);

        for (var entry : professors) {
            var prof = (Map<String, Object>) entry;
            String name = (String) prof.get("name");
            if (name == null) continue;
            String key = normalizeRmpName(name);
            if (key != null) {
                byNormalizedName.put(key, prof);
            }
        }
    }

    /**
     * Looks up a professor by their course_data faculty name.
     *
     * @param courseDataName faculty name in "Last, First M." format
     * @return the matching professor map from professors.json, or null if not found
     */
    public Map<String, Object> findByCourseDataName(String courseDataName) {
        String key = normalizeCourseDataName(courseDataName);
        if (key == null) return null;
        return byNormalizedName.get(key);
    }

    /**
     * Normalizes a course_data faculty name ("Last, First M.") to "firstname lastname".
     * Returns null if the name cannot be parsed.
     */
    static String normalizeCourseDataName(String name) {
        if (name == null) return null;
        // Format: "Last, First M." or "Last, First"
        int commaIdx = name.indexOf(',');
        if (commaIdx < 0) return null;
        String last = name.substring(0, commaIdx).trim();
        String rest = name.substring(commaIdx + 1).trim();
        // rest may be "First M." or "First" — take the first token
        String first = rest.split("\\s+")[0];
        if (last.isEmpty() || first.isEmpty()) return null;
        return (first + " " + last).toLowerCase();
    }

    /**
     * Normalizes a professors.json name ("First Last") to "firstname lastname".
     * Returns null if the name cannot be parsed.
     */
    static String normalizeRmpName(String name) {
        if (name == null) return null;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return null;
        return trimmed.toLowerCase();
    }

    /**
     * Returns all professor entries loaded from professors.json.
     */
    public List<Map<String, Object>> getAllProfessors() {
        return new ArrayList<>(byNormalizedName.values());
    }
}
