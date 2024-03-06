package com.lingvosphere.backend.Models;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    private String uid;
    private String pwd;
    private String email;
    private String profile;
    private String uname;
    private Map<String,Integer> level;
    private String token;
    private ArrayList<String> languages;
    private int character;
    private boolean verified;
    private List<Post> posts;
    private String cert_uri;

    public User(String email, String uid, String pwd, Map<String,Integer> level) {
        this.uid = uid;
        this.pwd = pwd;
        this.email = email;
        this.level = level;
    }

    public User(String email, String uid, String pwd, Map<String,Integer> level, String cert_uri) {
        this.uid = uid;
        this.pwd = pwd;
        this.email = email;
        this.level = level;
        this.cert_uri = cert_uri;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String avatar) {
        this.profile = avatar;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = email;
    }

    public Map getLevel() {
        return level;
    }

    public void setLevel(Map level) {
        this.level = level;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public int getCharacter() {
        return character;
    }

    public void setCharacter(int character) {
        this.character = character;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean getVerified() {
        return verified;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    // Method to add a post to the list
    public void addPost(Post post) {
        if (posts == null) {
            posts = new ArrayList<>();
        }
        posts.add(post);
    }

    public void setCert_uri(String cert_uri) {
        this.cert_uri = cert_uri;
    }

    public String getCert_uri(String cert_uri) {
        return cert_uri;
    }

    // toDocument method to convert User object into MongoDB document
    public Document toDocument() {
        Document doc = new Document("uid", uid)
                .append("pwd", pwd)
                .append("email", email)
                .append("level", level)
                .append("token", token)
                .append("languages", languages)
                .append("character", character)
                .append("verified", verified)
                .append("profile", profile)
                .append("uname", uname)
                .append("cert_uri", cert_uri);
    
        // Convert posts to a list of documents
        if (posts != null && !posts.isEmpty()) {
            List<Document> postsDocuments = new ArrayList<>();
            for (Post post : posts) {
                postsDocuments.add(post.toDocument());
            }
            doc.append("posts", postsDocuments);
        }
    
        return doc;
    }
    
}
