package com.lingvosphere.backend.Models;

import org.bson.Document;

public class MentorSession {
    private String sessionId;
    private String uid;
    private int year, month, dayOfMonth;

    public MentorSession(String sessionId, String uid, int year, int month, int dayOfMonth){
        this.sessionId = sessionId;
        this.uid = uid;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUid() {
        return uid;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    // toDocument method to convert MentorSession object into MongoDB document
    public Document toDocument() {
        Document doc = new Document("sessionId", sessionId)
                .append("uid", uid)
                .append("year", year)
                .append("month", month)
                .append("dayOfMonth", dayOfMonth);

        return doc;
    }
}
