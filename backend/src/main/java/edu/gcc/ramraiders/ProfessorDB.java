package edu.gcc.ramraiders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * Loads professors.json (RateMyProfessors data) and exposes it for lookup and listing.
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
