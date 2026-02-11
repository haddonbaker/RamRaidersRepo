package edu.gcc.ramraiders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void login() {
        Student s = new Student();
        assertEquals(-1,s.login("janepond","dr.wagner"));
    }
}