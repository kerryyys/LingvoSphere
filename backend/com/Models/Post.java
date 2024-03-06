package com.lingvosphere.backend.Models;

import java.util.List;

import org.bson.Document;
import java.util.ArrayList;

public class Post {
        private String postId;
        private String description;
        private String imageUrl;
        private List<Comment> comments;

        public Post(String postId, String description, String imageUrl, List<Comment> comments) {
            this.postId = postId;
            this.description = description;
            this.imageUrl = imageUrl;
            this.comments = comments;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public List<Comment> getComments() {
            return comments;
        }

        public void setComments(List<Comment> comments) {
            this.comments = comments;
        }


        // toDocument method to convert Post object into MongoDB document
        public Document toDocument() {
            Document doc = new Document("postId", postId)
                    .append("description", description)
                    .append("imageUrl", imageUrl);

            // Convert comments to a list of documents
            if (comments != null && !comments.isEmpty()) {
                List<Document> commentsDocuments = new ArrayList<>();
                for (Comment comment : comments) {
                    commentsDocuments.add(comment.toDocument());
                }
                doc.append("comments", commentsDocuments);
            }

            return doc;
        }
    }
