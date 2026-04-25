package edu.gcc.ramraiders;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void loginReturnsNullWhenStudentMissing() {
        // If StudentDB.load cannot find a student, login should return null
        assertNull(Student.login("nonexistent_user", "password"));
    }

    @Test
    void logoutClearsAllItNeedsTo() throws Exception {
        Student s = new Student();
        Field pwField = Student.class.getDeclaredField("passwordHash");
        pwField.setAccessible(true);
        Field saltField = Student.class.getDeclaredField("salt");
        saltField.setAccessible(true);

        pwField.set(s, "HASH");
        saltField.set(s, "SALT");

        s.logout();

        assertNull(pwField.get(s));
        assertNull(saltField.get(s));
    }

    @Test
    void setMyScheduleAndGetItBack() {
        Student s = new Student();
        Schedule sch = new Schedule();
        s.setMySchedule(sch);
        assertSame(sch, s.getSchedule("default"));
        assertTrue(s.getSchedules().containsKey("default"));
    }

    @Test
    void usernameAndDisplayName() throws Exception {
        Student s = new Student();
        Field userField = Student.class.getDeclaredField("username");
        userField.setAccessible(true);
        Field dispField = Student.class.getDeclaredField("displayName");
        dispField.setAccessible(true);

        userField.set(s, "Mr. John Deer");
        dispField.set(s, "Mrs. Jane Doe");

        assertEquals("Mr. John Deer", s.getUsername());
        assertEquals("Mrs. Jane Doe", s.getDisplayName());
    }

}
