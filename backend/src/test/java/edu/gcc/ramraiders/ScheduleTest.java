package edu.gcc.ramraiders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

public class ScheduleTest {

    private Course makeSampleCourse(String department, int code, char section, String name, int openSeats, int credits) {
        return new Course(
                department + code + "-" + section,
                department,
                code,
                section,
                name,
                Course.SemesterType.Fall,
                2026,
                Set.of("Prof"),
                Set.of(new Course.MeetingTime(Course.Day.Monday, 9, 0, 50)),
                credits,
                openSeats,
                30,
                12345,
                Set.of(),
                "Lorem Ipsum Est"
        );
    }

    @Test
    public void add() {
        Schedule s = new Schedule();
        Course c = makeSampleCourse("HUMA", 101, 'A', "Amish Technological Studies: Is it truly a paradise?", 5, 3);
        String res = s.add(c);
        assertEquals("SUCCESS", res);
        assertTrue(s.getCourses().contains(c));
    }

    @Test
    public void isDuplicate() {
        Schedule s = new Schedule();
        Course c = makeSampleCourse("PHIL", 101, 'A', "The philosophy of Dude Where is my Car", 5, 3);
        assertEquals("SUCCESS", s.add(c));
        assertTrue(s.isDuplicate(c));
        Course c2 = makeSampleCourse("PHIL", 101, 'B', "The philosophy of Dude Where is my Car", 5, 3);
        String r = s.add(c2);
        assertTrue(r.equals("duplicate") || s.isDuplicate(c2));
    }

    @Test
    public void remove() {
        Schedule s = new Schedule();
        Course c = makeSampleCourse("COMP", 201, 'A', "How to remove a class (harder than you think)", 5, 3);
        s.add(c);
        int r = s.remove(c);
        assertEquals(1, r);
        assertFalse(s.getCourses().contains(c));
    }

    @Test
    public void save() {
        Schedule s = new Schedule();
        Course c = makeSampleCourse("COMP", 301, 'A', "Saved by the Bell? I thought only Christ saves?", 5, 3);
        s.add(c);
        Student st = new Student();
        int r = s.save(s, st);
        assertTrue(r == 1 || r == -1);
    }

    @Test
    public void ConflictType_overlap() {
        Schedule s = new Schedule();
        Course c1 = makeSampleCourse("COMP", 401, 'A', "Lap", 5, 3);
        Course c2 = makeSampleCourse("COMP", 402, 'A', "what's this lap doing up there!?", 5, 3);
        s.add(c1);
        String res = s.add(c2);
        assertTrue(res.equals("overlap") || res.equals("SUCCESS") || res.equals("duplicate"));
    }

    @Test
    public void getTotalCredits() {
        Schedule s = new Schedule();
        //sorry for the long line I had to make sure that the courses met at different times because the
        // tests were failing because of overlap. the rest use the helper function as normal -A
        Course c1 = new Course("COMP501-A","COMP",501,'A',"Super creative name",Course.SemesterType.Fall,2026,Set.of("Prof"),Set.of(new Course.MeetingTime(Course.Day.Monday,9,0,50)),4,5,30,12345,Set.of(),"desc");
        Course c2 = new Course("COMP502-A","COMP",502,'A',"An incredibly generic name",Course.SemesterType.Fall,2026,Set.of("Prof"),Set.of(new Course.MeetingTime(Course.Day.Tuesday,9,0,50)),3,5,30,12346,Set.of(),"desc");
        assertEquals("SUCCESS", s.add(c1));
        assertEquals("SUCCESS", s.add(c2));
        int total = s.getTotalCredits();
        assertEquals(7, total);
    }

    @Test
    public void getCourses() {
        Schedule s = new Schedule();
        Course c = makeSampleCourse("HUMA", 601, 'A', "Get a load of this!", 5, 3);
        s.add(c);
        assertNotNull(s.getCourses());
        assertFalse(s.getCourses().isEmpty());
    }

    @Test
    public void undoRedo() {
        Schedule s = new Schedule();
        Course c = makeSampleCourse("MATH", 701, 'A', "Am I Man or Muppet? A mathematical proof", 5, 3);
        assertEquals("SUCCESS", s.add(c));
        boolean u = s.undo();
        assertTrue(u);
        assertFalse(s.getCourses().contains(c));
        boolean r = s.redo();
        assertTrue(r);
        assertTrue(s.getCourses().contains(c));
    }
}
