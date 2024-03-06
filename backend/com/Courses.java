package com.lingvosphere.backend;

import com.lingvosphere.backend.Models.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Courses {
    public static ArrayList<Course> courses = new ArrayList<>();
    public static Map<String, ArrayList<Integer>> languages = new HashMap<>();

    public static String getCourseName(int idx) {
        for(Course course : courses) {
            if(course.courseId == idx)
                return course.courseName;
        }
        return "";
    }

    public static Course getCourse(int courseId) {
        for(Course course : courses) {
            if(course.courseId == courseId)
                return course;
        }
        return null;
    }

}
