package edu.gcc.ramraiders;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    private Course makeSampleCourse(String department, int code, char section, String name) {
        return new Course(
                department + code + "-" + section, department, code, section,
                name, Course.SemesterType.Fall, 2026, Set.of("Prof"),
                Set.of(new Course.MeetingTime(Course.Day.Monday, 9, 0, 50)),
                3, 10, 30, 12345, Set.of(), "I am sick of writing these after having them deleted");
    }

    @Test
    public void getId() {
        Course c = makeSampleCourse("COMP", 101, 'A', "Intro to Goblty Gook 2 (The electric boogaloo)");
        assertEquals("COMP101-A", c.getId());
    }

    @Test
    public void toStringAndCompareTo() {
        Course a = makeSampleCourse("COMP", 100, 'A', "Intro to NUMBERS");
        Course b = makeSampleCourse("HUMA", 101, 'A', "SWEET!! Potatoes!! A cultural history");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertNotEquals(a, b);
        String ts = a.toString();
        assertTrue(ts.contains("COMP"));
        assertTrue(ts.contains("NUMBERS"));

        String fs = b.toString();
        assertFalse(fs.contains("HUMkkA"));
        assertFalse(fs.contains("SWnEET"));
    }

    @Test
    public void SuggestAlternatives() throws Exception {
        Course target = makeSampleCourse("ENG", 101, 'A', "Gundam a Literary Perspective");
        Course alt = makeSampleCourse("ENG", 101, 'B', "Gundam a Literary Perspective");

        // This creates a CourseDB instance via and course list -A
        var cdClass = CourseDB.class;
        var ctor = cdClass.getDeclaredConstructor();
        ctor.setAccessible(true);
        CourseDB db = ctor.newInstance();
        var courseListField = cdClass.getDeclaredField("courseList");
        courseListField.setAccessible(true);
        java.util.List<Course> list = new java.util.ArrayList<>();
        list.add(alt);
        list.add(target);
        courseListField.set(db, list);

        //I was having trouble with Null pointer exceptions, so I added these sets below to help  -A
        var possibleDepartmentsField = cdClass.getDeclaredField("possibleDepartments");
        possibleDepartmentsField.setAccessible(true);
        possibleDepartmentsField.set(db, new java.util.TreeSet<>(java.util.Set.of("ENG","COMP","HUMA")));

        var possibleYearsField = cdClass.getDeclaredField("possibleYears");
        possibleYearsField.setAccessible(true);
        possibleYearsField.set(db, new java.util.TreeSet<>(java.util.Set.of(2026)));

        var possibleProfessorsField = cdClass.getDeclaredField("possibleProfessors");
        possibleProfessorsField.setAccessible(true);
        possibleProfessorsField.set(db, new java.util.TreeSet<>(java.util.Set.of("Prof")));

        var possibleCreditsField = cdClass.getDeclaredField("possibleCredits");
        possibleCreditsField.setAccessible(true);
        possibleCreditsField.set(db, new java.util.TreeSet<>(java.util.Set.of(3)));


        SearchController.search = new Search(db);

        Schedule s = new Schedule();
        var alternatives = target.SuggestAlternatives(target, s);
        assertNotNull(alternatives);
        assertTrue(alternatives.contains(alt));
    }
}
