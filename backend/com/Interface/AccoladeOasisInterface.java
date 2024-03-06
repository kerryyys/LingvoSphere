package com.lingvosphere.backend.Interfaces;

import com.lingvosphere.backend.Database;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.model.Projections;
import xyz.Blockers.Utils.RestfulAPIServer.APIHandler;
import xyz.Blockers.Utils.RestfulAPIServer.Context;
import xyz.Blockers.Utils.RestfulAPIServer.RestfulAPIServer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Map;

public class AccoladeOasisInterface {
    private static MongoCollection<Document> collection = Database.getCollection("lingvosphere", "courseThreads");
    private static final String KEY_CODE = "code";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_COURSE_ID = "courseID";
    private static final String KEY_MSG = "msg";

    public static void init(RestfulAPIServer server) {
        server.GET("/api/accoladeoasis/plantgrowth", new APIHandler() {
            @Override
            public void handle(Context context) {
                getPlantGrowth(context);
            }
        });
    }

    private static void getPlantGrowth(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String courseIDString = params.get("courseID");

        if (uid != null && courseIDString != null) {
            int courseID = Integer.parseInt(courseIDString);
            System.out.println("Received request for uid: " + uid + ", courseID: " + courseID);

            Document courseStatus = getCourseStatus(uid, courseID);

            if (courseStatus != null) {
                int currentProgress = courseStatus.getInteger("progress", 0);

                JSONObject jsonResponse = new JSONObject();
                try {
                    jsonResponse.put(KEY_CODE, 0);
                    jsonResponse.put(KEY_PROGRESS, currentProgress);
                    jsonResponse.put(KEY_COURSE_ID, courseID);
                    jsonResponse.put(KEY_MSG, "Plant growth updated successfully for courseID: " + courseID);
                } catch (JSONException e) {
                    e.printStackTrace();
                    context.JSON("{\"code\":-1, \"msg\":\"Error creating JSON response\"}");
                }

                context.JSON(jsonResponse.toString());
            } else {
                JSONObject errorResponse = new JSONObject();
                try {
                    errorResponse.put("code", -1);
                    errorResponse.put("msg", "Course status not found for user and language");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                context.JSON(errorResponse.toString());
            }
        } else {
            context.Text("{\"code\":-1, \"msg\":\"Missing uid or courseID parameter\"}");
        }
    }

    private static Document getCourseStatus(String uid, int courseID) {
        return collection.find(and(eq("uid", uid), eq("courseId", courseID)))
                .projection(Projections.fields(Projections.include("progress"), Projections.excludeId()))
                .first();
    }
}
