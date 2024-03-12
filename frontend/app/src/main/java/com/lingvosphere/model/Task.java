package com.lingvosphere.model;

public class Task {
    private long timestamp;
    private String name;
    private int id;
    private int type;

    private String course;

    public Task(long timestamp, String name, int id, int type, String course) {
        this.timestamp = timestamp;
        this.name = name;
        this.id = id;
        this.type = type;
        this.course = course;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
