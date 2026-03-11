package edu.gcc.ramraiders;

import java.util.*;

public class Search {

    private Set<Course> results;

    public Search(List<Course> courseList) {
        this.results = new HashSet<>(courseList);
    }

    /**
     * @return The courses selected by the current filter
     */
    public Set<Course> getCourses() {
        return results;
    }

    /**
     * Updates the search results' filter
     * @param filter The filter
     */
    public void applyFilter(Filter filter) {
    }
}
