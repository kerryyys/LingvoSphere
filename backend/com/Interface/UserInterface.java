package com.lingvosphere.backend.Interfaces;
import com.lingvosphere.backend.Database;
import com.lingvosphere.backend.Models.User;
import com.lingvosphere.backend.Utils.RandomUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.Blockers.Utils.RestfulAPIServer.APIHandler;
import xyz.Blockers.Utils.RestfulAPIServer.Context;
import xyz.Blockers.Utils.RestfulAPIServer.RestfulAPIServer;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInterface {

    private static boolean isStrongPassword(String pwd){
        String PASSWORD_REGEX = "^(?=.*[0-9])" + "(?=.*[a-zA-Z])" + "(?=.*[!*@#$%^&+=])" + "(?=\\S+$).{6,20}$";
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        if(pwd == null){
            return false;
        }
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    private static boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static boolean checkToken(String uid, String token) {

        // Validate input
        if (uid == null || token == null)
            return false;

        // Access the MongoDB collection
        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        if (userDoc == null) {
            // User not found
            return false;
        } else {
            // Check if the token matches
            String storedToken = userDoc.getString("token");
            if (token.equals(storedToken)) {
                // Token is valid
                return true;
            } else {
                // Token is invalid
                return false;
            }
        }
    }

    private static void setUname(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String uname = params.get("uname");

        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        if (userDoc == null) {
            // User not found
            context.Text("{\"code\":-1, \"msg\":\"Invalid uid\"}");
            return;
        }

        // Update the user's uname
        Bson updateOperation = set("uname", uname);
        collection.updateOne(eq("uid", uid), updateOperation);

        // Respond with a success message
        context.Text("{\"code\":0, \"msg\":\"Username updated successfully.\"}");
    }

    private static void getUname(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        if (userDoc == null) {
            // User not found
            context.Text("{\"code\":-1, \"msg\":\"Invalid uid\"}");
            return;
        }

        // Get the user's uname
        String uname = userDoc.getString("uname");

        // Respond with uname
        context.Text("{\"code\":0, \"uname\":\"" + uname + "\"}");
    }


    public static void init(RestfulAPIServer server) {

        server.GET("/api/user/login", new APIHandler() {
            @Override
            public void handle(Context context) {
                LoginUser(context);
            }
        });

        server.GET("/api/user/register", new APIHandler() {
            @Override
            public void handle(Context context) {
                RegisterUser(context);
            }
        });

        server.GET("/api/user/logout", new APIHandler() {
            @Override
            public void handle(Context context) {
                LogoutUser(context);
            }
        });

        server.POST("/api/user/forgotpwd", new APIHandler() {
            @Override
            public void handle(Context context) {
                ForgotPwd(context);
            }
        });

        server.GET("/api/user/checkToken", new APIHandler() {
            @Override
            public void handle(Context context) {
                checkToken(context);
            }
        });

        server.GET("/api/user/setProfile", new APIHandler() {
            @Override
            public void handle(Context context) {
                setProfile(context);
            }
        });

        server.GET("/api/user/getProfile", new APIHandler() {
            @Override
            public void handle(Context context) {
                getProfile(context);
            }
        });

        server.GET("/api/user/getUname", new APIHandler() {
            @Override
            public void handle(Context context) {
                getUname(context);
            }
        });

        server.GET("/api/user/setUname", new APIHandler() {
            @Override
            public void handle(Context context) {
                setUname(context);
            }
        });

        server.POST("/api/user/createPost", new APIHandler() {
            @Override
            public void handle(Context context) {
                createPost(context);
            }
        });

        server.GET("/api/user/getPost", new APIHandler() {
            @Override
            public void handle(Context context) {
                getPost(context);
            }
        });

        server.GET("/api/user/getComments", new APIHandler() {
            @Override
            public void handle(Context context) {
                getComments(context);
            }
        });
        
        // Endpoint to add a comment to a post
        server.POST("/api/user/addComment", new APIHandler() {
            @Override
            public void handle(Context context) {
                addComment(context);
            }
        });
    }

    private static void getProfile(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        if (userDoc == null) {
            // User not found
            context.Text("{\"code\":-1, \"msg\":\"Invalid uid\"}");
            return;
        }

        String profile = userDoc.getString("profile");

        // Respond with a success message
        context.Text("{\"code\":0, \"data\":\"" + profile + "\"}");
    }

    private static void setProfile(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String profile = params.get("profile");
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        if (userDoc == null) {
            // User not found
            context.Text("{\"code\":-1, \"msg\":\"Invalid uid\"}");
            return;
        }

        Bson updateOperation = set("profile", profile);
        collection.updateOne(eq("uid", uid), updateOperation);

        // Respond with a success message
        context.Text("{\"code\":0, \"msg\":\"Profile updated successfully.\"}");
    }

    private static void checkToken(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (uid == null || token == null) {
            context.Text("{\"code\":-1, \"msg\":\"Missing uid or token\"}");
            return;
        }

        // Access the MongoDB collection
        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        if (userDoc == null) {
            // User not found
            context.Text("{\"code\":-1, \"msg\":\"Invalid uid\"}");
        } else {
            // Check if the token matches
            String storedToken = userDoc.getString("token");
            if (token.equals(storedToken)) {
                // Token is valid
                context.Text("{\"code\":0, \"msg\":\"Valid token\"}");
            } else {
                // Token is invalid
                context.Text("{\"code\":-1, \"msg\":\"Invalid token\"}");
            }
        }
    }

    private static void LoginUser(Context context) {
        Map<String, String> params = context.getParams();
        String email = params.get("email");
        String pwd = params.get("pwd");


        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");
        Document doc = collection.find(eq("email", email))
                .first();

        if (doc == null) { // User doesn't exist
            context.Text("{\"code\":-1, \"msg\":\"email " + email + " doesn't exist!\"}");
            return;
        }

        String password = doc.getString("pwd");
        if (pwd == null) {
            context.Text("{\"please\" key\" in\" password!\"}");
        }

        if (password.equals(pwd)) {
            String token = RandomUtil.generateToken(12);
            Bson filter = Filters.eq("email", email);
            Bson updateOperation = set("token", token);
            collection.updateOne(filter, updateOperation);
            context.Text("{\"code\":0, \"token\":\"" + token + "\", \"msg\":\"success!\", \"uid\":\"" + doc.getString("uid") + "\"}");
        } else {
            context.Text("{\"code\":-1, \"msg\":\"Invalid password!\"}");
        }
    }

    private static String generateUID() {
        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");
        while (true) {
            String uid = RandomUtil.generateToken(6);
            Document doc = collection.find(eq("uid", uid))
                    .first();
            if(doc == null)
                return uid;
        }
    }

    private static void RegisterUser(Context context) {
        Map<String, String> params = context.getParams();
        String uname = params.get("uname");
        String pwd = params.get("pwd");
        String email = params.get("email");
        String character = params.get("character");
        String cert_uri = params.get("cert_uri");
        String uid = generateUID();

        if(!isStrongPassword(pwd)){
            if(pwd.length()<6){
                context.Text("Password length must be more than 6.");
            }else{
                context.Text("Please include at least one digit, one special character, and one letter");
            }
        }

        if(!isValidEmail(email)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid Email\"}");
            return;
        }
        // Check if both email and password are provided
        if (email == null || pwd == null) {
            context.Text("{\"code\":-1, \"msg\":\"Email and password are required for registration!\"}");
            return;
        }

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");
        Document doc = collection.find(eq("email", email))
                .first();

        if (doc != null) { // user already exist
            context.Text("{\"code\":-1, \"msg\":\"email " + email + " already exist!\"}");
            return;
        }

        Map<String, Integer> levelMap = new HashMap<>();
        levelMap.put("DefaultLanguage", 1);//set language and level to default

        User user = new User(email, uid, pwd, levelMap);
        user.setUname(uname);
        user.setCharacter(Integer.parseInt(character));
        // Todo: add email verification ...
        user.setVerified(true);
        user.setCert_uri(cert_uri);

        collection.insertOne(user.toDocument());

        context.Text("{\"code\":0, \"msg\":\"Registration successful!\"}");
    }

    private static void LogoutUser(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        if (uid == null || token == null) {
            context.Text("{\"code\":-1, \"msg\":\"Please provide uid and token to logout!\"}");
            return;
        }

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        Document userDoc;

        if (uid != null) {
            // Logout using uid
            userDoc = collection.find(eq("uid", uid)).first();
        } else {
            // Logout using token
            userDoc = collection.find(eq("token", token)).first();
        }

        if (userDoc != null) {
            Bson filter = eq(uid != null ? "uid" : "token", uid != null ? uid : token);
            Bson updateOperation = set("token", null);
            collection.updateOne(filter, updateOperation);

            context.Text("{\"code\":0, \"msg\":\"Logout successful!\"}");
        } else {
            context.Text("{\"code\":-1, \"msg\":\"Invalid uid, token, or user not found!\"}");
        }
    }

    private static void ForgotPwd(Context context) {
        try {
            // Add logging
            System.out.println("Received request to /api/user/forgotpwd with parameters: " + context.getParams());

            // Assuming you have a Users collection in your database
            MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

            //Get user input (email for password reset)
            Map<String, String> params = context.getParams();
            String email = params.get("email");

            //check if the email exists in the database
            Document userDoc = collection.find(eq("email", email)).first();

            if (userDoc == null) { // user not found
                context.Text("{\"code\":-1, \"msg\":\"email \"not found!\"}");
                return;
            }

            if (userDoc != null) {
                //Generate a unique token for pwd reset
                String resetToken = RandomUtil.generateToken(12);

                //Store the reset Token in the database
                Bson filter = Filters.eq("email", email);
                Bson updateOperation = set("resetToken", resetToken);
                collection.updateOne(filter, updateOperation);

                // Construct the password reset link
                String resetLink = "http://localhost:63343/LingvoSphere_Backend1/com/lingvosphere/backend/Interfaces/ForgotPasswordWebPage.html?email=" + email + "&token=" + resetToken;

                context.Text("{\"code\":0, \"msg\":\"Password reset instructions sent to your email!\"}");
            }

        }catch (Exception e){
            e.printStackTrace();
            context.Text("{\"code\":-1, \"msg\":\"An error occurred during password reset.\"}");
        }

    }

    // Community corner create post
    // Community corner create post
private static void createPost(Context context) {
    Map<String, String> params = context.getParams();
    String uid = params.get("uid");
    String token = params.get("token");

    if (!checkToken(uid, token)) {
        context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
        return;
    }

    // Access the MongoDB collection for posts
    MongoCollection<Document> postCollection = Database.getCollection("lingvosphere", "posts");

    // Generate a unique postId
    String postId = RandomUtil.generateToken(12);

    String postText = "";
    String imageUrl = "";

    try {
        postText = params.get("postText");
    } catch (NullPointerException e) {
        e.printStackTrace();
    }

    try {
        imageUrl = params.get("imageUrl");
    } catch (NullPointerException e) {
        e.printStackTrace();
    }

    // Create the new post document
    Document newPost = new Document()
            .append("postId", postId)
            .append("uid", uid);

    if (postText != null && !postText.isEmpty()) {
        newPost.append("postText", postText);
    }

    if (imageUrl != null && !imageUrl.isEmpty()) {
        newPost.append("imageUrl", imageUrl);
    }

    // Insert the new post document
    postCollection.insertOne(newPost);

    // Respond with the postId
    context.Text("{\"code\":0, \"msg\":\"Post created successfully.\", \"postId\":\"" + postId + "\"}");
}


    private static void getPost(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        // Access the MongoDB collection for posts
        MongoCollection<Document> postCollection = Database.getCollection("lingvosphere", "posts");

        // Access the MongoDB collection for users
        MongoCollection<Document> userCollection = Database.getCollection("lingvosphere", "users");

        // Find all posts
        List<Document> allPosts = postCollection.find().into(new ArrayList<>());

        // Convert allPosts to a list of maps with usernames
        List<Map<String, Object>> postsList = new ArrayList<>();
        for (Document postDoc : allPosts) {
            Map<String, Object> postMap = new HashMap<>();

            // Iterate through the keys of postDoc
            for (String key : postDoc.keySet()) {
                postMap.put(key, postDoc.get(key));
            }

            String postByUid = postDoc.get("uid").toString();
            String uname = "Unknown";
            String profile = "no_profile";

            // Find the user document by uid
            Document userDoc = userCollection.find(eq("uid", postByUid)).first();

            if (userDoc != null) {
                // Get the user's uname
                uname = userDoc.getString("uname");
                // Check if the "profile" field exists in the document
                if (userDoc.containsKey("profile") && userDoc.get("profile") != null) {
                    profile = userDoc.getString("profile");
                }
            }

            postMap.put("username", uname);
            postMap.put("profile", profile);
            postsList.add(postMap);
        }

        // Respond with all posts
        try {
            String response = new JSONObject()
                    .put("code", 0)
                    .put("msg", "All posts retrieved successfully.")
                    .put("posts", postsList)
                    .toString();
            context.Text(response);
        } catch (JSONException e) {
            e.printStackTrace();
            context.Text("{\"code\":-1, \"msg\":\"Error processing server response.\"}");
        }
    }


    private static void getComments(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String postId = params.get("postId");
    
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }
    
        // Access the MongoDB collection for comments
        MongoCollection<Document> commentCollection = Database.getCollection("lingvosphere", "comments");
    
        // Access the MongoDB collection for users
        MongoCollection<Document> userCollection = Database.getCollection("lingvosphere", "users");
    
        // Filter comments by postId
        List<Document> commentsForPost = commentCollection.find(eq("postId", postId)).into(new ArrayList<>());
    
        // Convert comments to a list of maps with usernames
        List<Map<String, Object>> commentsList = new ArrayList<>();
        for (Document commentDoc : commentsForPost) {
            Map<String, Object> commentMap = new HashMap<>();
    
            // Iterate through the keys of commentDoc
            for (String key : commentDoc.keySet()) {
                commentMap.put(key, commentDoc.get(key));
            }
    
            String postByUid = commentDoc.get("uid").toString();
            String uname = "Unknown";
            String profile = "no_profile";
    
            // Find the user document by uid
            Document userDoc = userCollection.find(eq("uid", postByUid)).first();
    
            if (userDoc != null) {
                // Get the user's uname
                uname = userDoc.getString("uname");
                // Check if the "profile" field exists in the document
                if (userDoc.containsKey("profile") && userDoc.get("profile") != null) {
                    profile = userDoc.getString("profile");
                }
            }
    
            commentMap.put("username", uname);
            commentMap.put("profile", profile);
            commentsList.add(commentMap);
        }
    
        // Respond with comments for the specified postId
        try {
            String response = new JSONObject()
                    .put("code", 0)
                    .put("msg", "Comments for the specified postId retrieved successfully.")
                    .put("comments", commentsList)
                    .toString();
            context.Text(response);
        } catch (JSONException e) {
            e.printStackTrace();
            context.Text("{\"code\":-1, \"msg\":\"Error processing server response.\"}");
        }
    }
    
    
    // Method to add a comment to a post
    private static void addComment(Context context) {
        Map<String, String> params = context.getParams();
        String postId = params.get("postId");
        String uid = params.get("uid");
        String commentText = params.get("commentText");
    
        // Access the MongoDB collection for posts
        MongoCollection<Document> commentCollection = Database.getCollection("lingvosphere", "comments");
    
        // Create the new comment document
        Document newComment = new Document()
                .append("postId", postId)
                .append("uid", uid)
                .append("commentText", commentText);
    
        // Insert the new post document
        commentCollection.insertOne(newComment);
    
        // Respond with the postId
        context.Text("{\"code\":0, \"msg\":\"Comment uploaded successfully.\", \"postId\":\"" + postId + "\"}");
    }
}


