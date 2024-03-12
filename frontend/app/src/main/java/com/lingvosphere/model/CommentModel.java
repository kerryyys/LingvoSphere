package com.lingvosphere.model;

public class CommentModel {

    String comment, username, profile;

    public CommentModel() {
    }

    public CommentModel(String username, String profile, String comment) {
        this.comment = comment;
        this.username = username;
        this.profile = profile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
