package edu.gcc.ramraiders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    @Test
    void searchAndTimeslotFiltering() throws Exception {
        // Build a minimal in-memory CourseDB by instantiating via reflection and populating its lists
        java.lang.reflect.Constructor<CourseDB> ctor = CourseDB.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        CourseDB db = ctor.newInstance();

        Course c1 = new Course(
                "COMP100-A",
                "COMP",
                100,
                'A',
                "Intro",
                Course.SemesterType.Fall,
                2024,
                java.util.Set.of("Smith"),
                java.util.Set.of(new Course.MeetingTime(Course.Day.Monday, 9, 0, 60)),
                3,
                5,
                30,
                0,
                java.util.Set.of(),
                "desc1"
        );

        Course c2 = new Course(
                "COMP200-A",
                "COMP",
                200,
                'A',
                "Advanced",
                Course.SemesterType.Fall,
                2024,
                java.util.Set.of("Jones"),
                java.util.Set.of(new Course.MeetingTime(Course.Day.Tuesday, 9, 0, 60)),
                4,
                5,
                30,
                0,
                java.util.Set.of(),
                "desc2"
        );

        // Inject into CourseDB.courseList
        java.lang.reflect.Field courseListField = CourseDB.class.getDeclaredField("courseList");
        courseListField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<Course> list = (java.util.List<Course>) courseListField.get(db);
        list.clear();
        list.add(c1);
        list.add(c2);

        // Populate helper sets used by query parsing
        java.lang.reflect.Field pdField = CourseDB.class.getDeclaredField("possibleDepartments");
        pdField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Set<String> pd = (java.util.Set<String>) pdField.get(db);
        pd.clear(); pd.add("COMP");

        java.lang.reflect.Field pyField = CourseDB.class.getDeclaredField("possibleYears");
        pyField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Set<Integer> py = (java.util.Set<Integer>) pyField.get(db);
        py.clear(); py.add(2024);

        java.lang.reflect.Field ppField = CourseDB.class.getDeclaredField("possibleProfessors");
        ppField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Set<String> pp = (java.util.Set<String>) ppField.get(db);
        pp.clear(); pp.addAll(java.util.Set.of("Smith","Jones"));

        java.lang.reflect.Field pcField = CourseDB.class.getDeclaredField("possibleCredits");
        pcField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Set<Integer> pc = (java.util.Set<Integer>) pcField.get(db);
        pc.clear(); pc.addAll(java.util.Set.of(3,4));

        // Create Search and run assertions
        Search s = new Search(db);

        var all = s.search("", null);
        assertTrue(all.contains(c1) && all.contains(c2));

        var nameRes = s.search("Intro", null);
        assertTrue(nameRes.contains(c1));
        assertFalse(nameRes.contains(c2));

        // Timeslot filter: only c1 fits Monday 9:00
        Filter.Timeslot ts = new Filter.Timeslot(Course.Day.Monday, 9, 0, 60);
        Filter filter = new Filter(java.util.Set.of(), java.util.Set.of(), java.util.Set.of(), java.util.Set.of(), java.util.Set.of(), java.util.Set.of(ts), java.util.Set.of(), false, java.util.Set.of());
        var tsRes = s.search("", filter);
        assertTrue(tsRes.contains(c1));
        assertFalse(tsRes.contains(c2));
    }


}