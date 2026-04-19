package edu.gcc.ramraiders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    @Test
    void Filter() {
        Student s = new Student();
        assertEquals(-1,s.login("janepond","dr.wagner"));
    }

    @Test
    void combine() {

    }


}