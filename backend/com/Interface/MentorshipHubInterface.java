package com.lingvosphere.backend.Interfaces;

import com.lingvosphere.backend.Database;
import com.lingvosphere.backend.Utils.RandomUtil;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.Blockers.Utils.RestfulAPIServer.APIHandler;
import xyz.Blockers.Utils.RestfulAPIServer.Context;
import xyz.Blockers.Utils.RestfulAPIServer.RestfulAPIServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class MentorshipHubInterface {
    public static void init(RestfulAPIServer server){
        server.POST("/api/mentorshiphub/aimentor", new APIHandler() {
            @Override
            public void handle(Context context) {
                aiMentor(context);
            }
        });

        server.POST("/api/mentorshiphub/selectmentor", new APIHandler() {
            @Override
            public void handle(Context context) {
                selectMentor(context);
            }
        });

        server.POST("/api/mentorshiphub/creatementorsession", new APIHandler() {
            @Override
            public void handle(Context context) {
                createMentorSession(context);
            }
        });

        server.GET("/api/mentorshiphub/getmentorsession", new APIHandler() {
            @Override
            public void handle(Context context) {
                getMentorSession(context);
            }
        });
    }

    // Todo: chat with AI mentor
    public static void aiMentor(Context context){
        Map<String,String> params = context.getParams();
        String uid = params.get("uid");

        MongoCollection<Document> collection = Database.getCollection("lingvosphere", "users");

        // Find the user document by uid
        Document userDoc = collection.find(eq("uid", uid)).first();

        String input = params.get("input");
        context.Text(chatGPT(input));
        // Prints out a response to the question.
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions"
                ;
        String apiKey = "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"; // API key goes here
        String model = "gpt-3.5-turbo"; // current model of chatgpt api

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // returns the extracted contents of the response.
            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }

    // Todo: select professional mentor
    public static void selectMentor(Context context){
        Map<String,String> params = context.getParams();
        String uid = params.get("uid");
    }

    // Todo: create professional mentor session
    public static void createMentorSession(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String date = params.get("date");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> mentorSessionCollection = Database.getCollection("lingvosphere", "mentorSessions");

        // Access MongoDB collection for mentor sessions
        Document userDoc = mentorSessionCollection.find(eq("uid", uid)).first();

        // Generate a unique sessionId
        String sessionId = RandomUtil.generateToken(12);

        // Create the new mentor session document
        Document newSession = new Document()
                .append("uid", uid)
                .append("token", token)
                .append("sessionId", sessionId)
                .append("date", date);

        // Insert the new mentor session document
        mentorSessionCollection.insertOne(newSession);

        // Respond with sessionId
        context.Text("{\"code\":0, \"msg\":\"Session booked successfully.\", \"sessionId\":\"" + sessionId + "\"}");
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

    // Todo: get booked mentor session
    public static void getMentorSession(Context context){
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> mentorSessionCollection = Database.getCollection("lingvosphere", "mentorSessions");
        MongoCollection<Document> userCollection = Database.getCollection("lingvosphere", "users");

        List<Document> allSessions = mentorSessionCollection.find().into(new ArrayList<>());

        // Convert allPosts to a list of maps with usernames
        List<Map<String, Object>> sessionLists = new ArrayList<>();
        for (Document sessionDoc : allSessions) {
            Map<String, Object> sessionMap = new HashMap<>();

            // Iterate through the keys of postDoc
            for (String key : sessionDoc.keySet()) {
                sessionMap.put(key, sessionDoc.get(key));
            }

            String sessionByUid = sessionDoc.get("uid").toString();
            String uname = "Unknown";

            // Find the user document by uid
            Document userDoc = userCollection.find(eq("uid", sessionByUid)).first();

            if (userDoc != null) {
                // Get the user's uname
                uname = userDoc.getString("uname");
            }

            sessionMap.put("username", uname);
            sessionLists.add(sessionMap);
        }

        // Respond with all posts
        try {
            String response = new JSONObject()
                    .put("code", 0)
                    .put("msg", "All sessions retrieved successfully.")
                    .put("sessions", sessionLists)
                    .toString();
            context.Text(response);
        } catch (JSONException e) {
            e.printStackTrace();
            context.Text("{\"code\":-1, \"msg\":\"Error processing server response.\"}");
        }
    }
}
