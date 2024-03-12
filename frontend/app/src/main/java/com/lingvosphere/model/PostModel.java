// Model for fetching the data of a post from our database

package com.lingvosphere.model;

public class PostModel {

    private String userName, profile, imageUrl, postDescription, postId;

    public PostModel() {
    }

    public PostModel(String userName, String profile, String postId, String imageUrl, String postDescription) {
        this.userName = userName;
        this.profile = profile;
        this.imageUrl = imageUrl;
        this.postDescription = postDescription;
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
