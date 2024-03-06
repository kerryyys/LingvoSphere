package com.lingvosphere.backend.Models;

import org.bson.Document;

public class Comment {
    private String userId;
    private String text;

    public Comment(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // toDocument method to convert Comment object into MongoDB document
    public Document toDocument() {
        return new Document("userId", userId)
                .append("text", text);
    }
}
