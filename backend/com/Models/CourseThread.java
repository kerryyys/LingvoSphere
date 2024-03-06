package com.lingvosphere.backend.Models;

import com.lingvosphere.backend.Courses;
import org.bson.Document;

import java.util.ArrayList;

public class CourseThread {
    private String uid;
    private int courseID;
    private int progress;
    private int status;
    private ArrayList<String> chapters;
    private int chapters_count;
    private int chapters_progress;
    private ArrayList<String> tests;
    private int tests_count;
    private int tests_progress;
    private ArrayList<String> chapters_schedule;
    private ArrayList<String> tests_schedule;

    // Constructor
    public CourseThread(String uid, int courseID) {
        this.uid = uid;
        this.courseID = courseID;
        this.progress = 0;
        this.status = 0;
        this.chapters = new ArrayList<>();
        for(Course.Chapter chapter : Courses.getCourse(courseID).chapters)
            chapters.add(chapter.title);
        this.chapters_count = chapters.size();
        this.chapters_progress = 0;
        this.tests = new ArrayList<>();
        for(Course.Exam test : Courses.getCourse(courseID).exams)
            tests.add(test.examTitle);
        this.tests_count = tests.size();
        this.tests_progress = 0;
        this.chapters_schedule = new ArrayList<>();
        this.tests_schedule = new ArrayList<>();
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<String> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<String> chapters) {
        this.chapters = chapters;
    }

    public int getChaptersCount() {
        return chapters_count;
    }

    public void setChaptersCount(int chapters_count) {
        this.chapters_count = chapters_count;
    }

    public int getChaptersProgress() {
        return chapters_progress;
    }

    public void setChaptersProgress(int chapters_progress) {
        this.chapters_progress = chapters_progress;
    }

    public ArrayList<String> getTests() {
        return tests;
    }

    public void setTests(ArrayList<String> tests) {
        this.tests = tests;
    }

    public int getTestsCount() {
        return tests_count;
    }

    public void setTestsCount(int tests_count) {
        this.tests_count = tests_count;
    }

    public int getTestsProgress() {
        return tests_progress;
    }

    public void setTestsProgress(int tests_progress) {
        this.tests_progress = tests_progress;
    }

    public ArrayList<String> getChaptersSchedule() {
        return chapters_schedule;
    }

    public void setChaptersSchedule(ArrayList<String> chapters_schedule) {
        this.chapters_schedule = chapters_schedule;
    }

    public ArrayList<String> getTestsSchedule() {
        return tests_schedule;
    }

    public void setTestsSchedule(ArrayList<String> tests_schedule) {
        this.tests_schedule = tests_schedule;
    }

    public Document toDocument() {
        Document doc = new Document("uid", uid)
                .append("courseId", courseID)
                .append("progress", progress)
                .append("status", status)
                .append("chapters", chapters)
                .append("chapters_count", chapters_count)
                .append("chapters_progress", chapters_progress)
                .append("tests", tests)
                .append("tests_count", tests_count)
                .append("tests_progress", tests_progress)
                .append("chapters_schedule", chapters_schedule)
                .append("tests_schedule", tests_schedule);
        return doc;
    }
}
