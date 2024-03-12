package com.lingvosphere.model;

public class ProgressCourseItem {
    private int flagResource;
    private String courseName;
    private String progressText;
    private int progressBarValue;

    public ProgressCourseItem(int flagResource, String courseName, int progress) {
        this.flagResource = flagResource;
        this.courseName = courseName;
        this.progressText = String.valueOf(progress);
        this.progressBarValue = progress;
    }

    // Getters and Setters
    public int getFlagResource() {
        return flagResource;
    }

    public void setFlagResource(int flagResource) {
        this.flagResource = flagResource;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public int getProgressBarValue() {
        return progressBarValue;
    }

    public void setProgressBarValue(int progressBarValue) {
        this.progressBarValue = progressBarValue;
    }
}

