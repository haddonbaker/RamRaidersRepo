package edu.gcc.ramraiders;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Student {

    private String username;

    // Password is stored as a PBKDF2 hash — never as plaintext.
    private String passwordHash;
    private String salt;

    private String advisorEmail;
    private String statusSheet;
    private String[] major;
    private String[] minor;

    // Schedules keyed by term, e.g. "Fall_2024"
    private Map<String, Schedule> schedules = new HashMap<>();

    /** No-arg constructor required by Jackson. */
    public Student() {}

    private Student(String username, String passwordHash, String salt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    /**
     * Creates a new account, hashes the password, and persists the student to disk.
     *
     * @return the new Student on success; null if the username is already taken or an error occurs
     */
    public static Student createAccount(String username, String password) {
        if (StudentDB.exists(username)) return null;
        try {
            String salt = generateSalt();
            String hash = hashPassword(password, salt);
            Student student = new Student(username, hash, salt);
            if (!StudentDB.save(student)) return null;
            Main.log.info("Created account for: " + username);
            return student;
        } catch (Exception e) {
            Main.log.error("Error creating account for '" + username + "': " + e);
            return null;
        }
    }

    /**
     * Verifies credentials against the stored student file.
     *
     * @return the loaded Student on success; null if credentials are wrong or student doesn't exist
     */
    public static Student login(String username, String password) {
        Student stored = StudentDB.load(username);
        if (stored == null) return null;
        try {
            String hash = hashPassword(password, stored.salt);
            if (!hash.equals(stored.passwordHash)) return null;
            Main.log.info("Student '" + username + "' logged in successfully");
            return stored;
        } catch (Exception e) {
            Main.log.error("Error during login for '" + username + "': " + e);
            return null;
        }
    }

    /** Clears sensitive data from memory (call when the user logs out). */
    public void logout() {
        this.passwordHash = null;
        this.salt = null;
        Main.log.info("Student '" + username + "' logged out successfully");
    }

    /**
     * Saves (or replaces) the schedule for a given term and persists the student to disk.
     *
     * @param termKey e.g. "Fall_2024"
     * @return true on success
     */
    public boolean saveSchedule(String termKey, Schedule schedule) {
        schedules.put(termKey, schedule);
        return StudentDB.save(this);
    }

    /** Returns the schedule for a given term key, or null if none has been saved. */
    public Schedule getSchedule(String termKey) {
        return schedules.get(termKey);
    }

    public void setMySchedule(Schedule s) {
        schedules.put("default", s);
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Schedule> getSchedules() {
        return schedules;
    }

    // -------------------------------------------------------------------------
    // Password hashing helpers (PBKDF2WithHmacSHA256, 65536 iterations, 256-bit key)
    // -------------------------------------------------------------------------

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return HexFormat.of().formatHex(saltBytes);
    }

    private static String hashPassword(String password, String saltHex) throws Exception {
        byte[] salt = HexFormat.of().parseHex(saltHex);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return HexFormat.of().formatHex(hash);
    }
}
