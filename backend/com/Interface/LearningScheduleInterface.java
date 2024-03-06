package com.lingvosphere.backend.Interfaces;

import com.lingvosphere.backend.Courses;
import com.lingvosphere.backend.Database;
import com.lingvosphere.backend.Models.Course;
import com.lingvosphere.backend.Models.CourseThread;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import xyz.Blockers.Utils.RestfulAPIServer.APIHandler;
import xyz.Blockers.Utils.RestfulAPIServer.Context;
import xyz.Blockers.Utils.RestfulAPIServer.RestfulAPIServer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LearningScheduleInterface {
    public static void init(RestfulAPIServer server) {

        server.GET("/api/learning_schedule/setScheduleWithPreferences", new APIHandler() {
            @Override
            public void handle(Context context) {
                setScheduleWithPreferences(context);
            }
        });
        server.GET("/api/learning_schedule/enrollCourse", new APIHandler() {
            @Override
            public void handle(Context context) {
                enrollCourse(context);
            }
        });
        server.GET("/api/learning_schedule/getEnrolledCourses", new APIHandler() {
            @Override
            public void handle(Context context) {
                getEnrolledCourses(context);
            }
        });
        server.GET("/api/learning_schedule/getCourseStatus", new APIHandler() {
            @Override
            public void handle(Context context) {
                getCourseStatus(context);
            }
        });
        server.GET("/api/learning_schedule/getCourses", new APIHandler() {
            @Override
            public void handle(Context context) {
                getCourses(context);
            }
        });
        server.GET("/api/learning_schedule/getActivities", new APIHandler() {
            @Override
            public void handle(Context context) {
                getActivities(context);
            }
        });

        server.GET("/api/learning_schedule/setSchedule", new APIHandler() {
            @Override
            public void handle(Context context) {
                setSchedule(context);
            }
        });

        server.GET("/api/learning_schedule/removeSchedule", new APIHandler() {
            @Override
            public void handle(Context context) {
                removeSchedule(context);
            }
        });

        server.GET("/api/learning_schedule/getSchedules", new APIHandler() {
            @Override
            public void handle(Context context) {
                getSchedules(context);
            }
        });

        server.GET("/api/learning_schedule/getSchedulesByTimestamp", new APIHandler() {
            @Override
            public void handle(Context context) {
                getSchedulesByTimestamp(context);
            }
        });

        server.GET("/api/learning_schedule/getCourseContent", new APIHandler() {
            @Override
            public void handle(Context context) {
                getCourseContent(context);
            }
        });

        server.GET("/api/learning_schedule/getExamContent", new APIHandler() {
            @Override
            public void handle(Context context) {
                getTestContent(context);
            }
        });

        server.GET("/api/learning_schedule/makeProgress", new APIHandler() {
            @Override
            public void handle(Context context) {
                makeProgress(context);
            }
        });
    }

    private static void getEnrolledCourses(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Check if user is already enrolled in this language
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        ArrayList<String> result = new ArrayList<>();
        for (Document thread : threads) {
            int courseId = thread.getInteger("courseId");
            result.add(Courses.getCourseName(courseId));
        }
        context.Text("{\"code\":0,\"data\":" + convertToJSONArray(result) + "}");
    }

    private static void getCourseStatus(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Check if user is already enrolled in this language
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        ArrayList<String> result = new ArrayList<>();
        for (Document thread : threads) {
            int total = thread.getInteger("chapters_count") + thread.getInteger("tests_count");
            int current = thread.getInteger("tests_progress") + thread.getInteger("chapters_progress");
            int progress_chapter = thread.getInteger("chapters_progress");
            int progress = current * 100 / total;
            int courseId = thread.getInteger("courseId");
            if (total > current)
                result.add("{\"course\":\"" + Courses.getCourseName(courseId)
                        + "\", \"progress\":" + String.valueOf(progress) + ", \"chapter\":\"" + Courses.getCourse(courseId).chapters.get(progress_chapter > 0 ? progress_chapter + 1 : 0).title + "\"}");
        }
        context.Text("{\"code\":0,\"data\":" + convertToJSONArray(result, "") + "}");
    }

    private static String convertToJSONArray(ArrayList<String> x) {
        String res = "[";
        for (String i : x) {
            res += ("\"" + i + "\"");
            if (i != x.get(x.size() - 1))
                res += ",";
        }
        return res + "]";
    }

    private static String convertToJSONArray(ArrayList<String> x, String wrap) {
        String res = "[";
        for (String i : x) {
            res += (wrap + i + wrap);
            if (i != x.get(x.size() - 1))
                res += ",";
        }
        return res + "]";
    }

    private static void getCourses(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        // Get Enrolled Courses
        ArrayList<Integer> enrolled = new ArrayList<>();
        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Check if user is already enrolled in this language
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        for (Document thread : threads) {
            int courseId = thread.getInteger("courseId");
            enrolled.add(courseId);
        }

        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Integer>> language : Courses.languages.entrySet()) {
            String category = language.getKey();
            ArrayList<String> courses = new ArrayList<>();
            ArrayList<Integer> courseIds = language.getValue();
            for (int x : courseIds) {
                String enroll_status = "false";
                for (int i : enrolled)
                    if (x == i)
                        enroll_status = "true";
                courses.add("{\"course\":\"" + Courses.getCourseName(x) + "\",\"courseId\":" + String.valueOf(x) + ",\"enrolled\":" + enroll_status + "}");
            }
            result.add("{\"language\":\"" + category + "\",\"data\":" + convertToJSONArray(courses, "") + "}");
        }

        String CoursesJSON = convertToJSONArray(result, "");

        context.Text("{\"code\":0, \"data\":" + CoursesJSON + "}");
    }

    private static void enrollCourse(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        int courseId = Integer.parseInt(params.get("courseId")); // courseId as an integer ID

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Check if user is already enrolled in this language
        FindIterable<Document> existingCourses = courseThreadsCollection.find(eq("uid", uid));
        for (Document course : existingCourses) {
            if (course.getInteger("courseId") == courseId) {
                context.Text("{\"code\":-1, \"msg\":\"Already enrolled in this course.\"}");
                return;
            }
        }

        // Create a new course thread for this language
        Document newCourseThread = new CourseThread(uid, courseId).toDocument();
        courseThreadsCollection.insertOne(newCourseThread);

        context.Text("{\"code\":0, \"msg\":\"Enrolled in course successfully.\"}");
    }


    private static void getActivities(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        // Access the MongoDB collection for course threads
        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find all course threads for the given user ID
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));

        List<Document> activitiesToComplete = new ArrayList<>();
        ArrayList<String> tests = new ArrayList<>();
        ArrayList<String> chapters = new ArrayList<>();
        for (Document thread : threads) {
            // Logic to check the progress of each course and test
            int chapters_count = thread.getInteger("chapters_count", 0);
            int tests_count = thread.get("tests_count", 0);
            try {
                int chapters_progress = ((List<String>) thread.get("chapters_schedule")).size();
                int tests_progress = ((List<String>) thread.get("tests_schedule")).size();
                int course_id = thread.getInteger("courseId");
                Course course = Courses.getCourse(course_id);

                if (chapters_count >= chapters_progress) {
                    String chapter_name = course.chapters.get(chapters_progress).title;
                    chapters.add("{\"name\":\"" + chapter_name + "\", \"courseName\":\"" + Courses.getCourse(course_id).courseName + "\", \"courseId\":\"" + String.valueOf(course_id) + "\"}");
                }

                if (tests_progress <= tests_count) {
                    String test_name = course.exams.get(tests_progress).examTitle;
                    tests.add("{\"name\":\"" + test_name + "\", \"courseName\":\"" + Courses.getCourse(course_id).courseName + "\", \"courseId\":\"" + String.valueOf(course_id) + "\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Convert the list of activities to a JSON array
        String chaptersJSON = convertToJSONArray(chapters, "");
        String testJSON = convertToJSONArray(tests, "");

        context.Text("{\"code\":0, \"chapters\":" + chaptersJSON + ",\"tests\":" + testJSON + "}");
    }


    private static void setSchedule(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String courseId = params.get("courseId"); // The ID of the course thread
        String scheduleTimestamp = params.get("scheduleTimestamp");
        String type = params.get("type");
        // Validate inputs
        if (!checkToken(uid, token) || courseId == null || scheduleTimestamp == null || type == null) {
            context.Text("{\"code\":-1, \"msg\":\"Incomplete parameters.\"}");
            return;
        }

        // Access the MongoDB collection for course threads
        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find the specific course thread
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        for (Document thread : threads) {
            if (thread.getInteger("courseId").equals(Integer.parseInt(courseId))) {
                Bson updateOperation;
                if (type.equals("0")) {
                    int tests_count = thread.getInteger("tests_count");
                    ArrayList<Integer> tests_schedule = thread.get("tests_schedule", ArrayList.class);
                    if (tests_schedule.size() == tests_count) {
                        context.Text("{\"code\":-1, \"msg\":\"Operation Failed!\"}");
                        return;
                    } else {
                        tests_schedule.add(Integer.parseInt(scheduleTimestamp));
                        updateOperation = Updates.set("tests_schedule", tests_schedule);
                    }
                } else {
                    int chapters_count = thread.getInteger("chapters_count");
                    ArrayList<Integer> chapters_schedule = thread.get("chapters_schedule", ArrayList.class);
                    if (chapters_schedule.size() == chapters_count) {
                        context.Text("{\"code\":-1, \"msg\":\"Operation Failed!\"}");
                        return;
                    } else {
                        chapters_schedule.add(Integer.parseInt(scheduleTimestamp));
                        updateOperation = Updates.set("chapters_schedule", chapters_schedule);
                    }
                }
                courseThreadsCollection.updateOne(eq("_id", thread.getObjectId("_id")), updateOperation);
                context.Text("{\"code\":0, \"msg\":\"Operation Succeeded!\"}");
                return;
            }
        }

        context.Text("{\"code\":-1, \"msg\":\"Not enrolled in the course!\"}");
    }

    private static void setScheduleWithPreferences(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String courseId = params.get("courseId"); // The ID of the course thread
        String days = params.get("days");
        // Validate inputs
        if (!checkToken(uid, token) || courseId == null || days == null) {
            context.Text("{\"code\":-1, \"msg\":\"Incomplete parameters.\"}");
            return;
        }

        // Access the MongoDB collection for course threads
        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find the specific course thread
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        for (Document thread : threads) {
            if (thread.getInteger("courseId").equals(Integer.parseInt(courseId))) {
                LocalDate today = LocalDate.now();
                int chapters_count = thread.getInteger("chapters_count");
                int tests_count = thread.getInteger("tests_count");
                ArrayList<Integer> chapters_schedule = thread.get("chapters_schedule", ArrayList.class);
                ArrayList<Integer> tests_schedule = thread.get("tests_schedule", ArrayList.class);
                for(int i = 0;i < chapters_count;i ++) {
                    while(days.charAt(today.getDayOfWeek().getValue()%7) == '0')
                        today = today.minusDays(-1);
                    ZonedDateTime zonedDateTime = today.atStartOfDay(ZoneId.systemDefault());
                    Instant instant = zonedDateTime.toInstant();
                    long epochMilli = instant.toEpochMilli();
                    long epochSecond = epochMilli / 1000;
                    chapters_schedule.add((int) epochSecond);
                    today = today.minusDays(-1);
                }
                for(int i = 0;i < tests_count;i ++) {
                    while(days.charAt(today.getDayOfWeek().getValue()%7) == '0')
                        today = today.minusDays(-1);
                    ZonedDateTime zonedDateTime = today.atStartOfDay(ZoneId.systemDefault());
                    Instant instant = zonedDateTime.toInstant();
                    long epochMilli = instant.toEpochMilli();
                    long epochSecond = epochMilli / 1000;
                    tests_schedule.add((int) epochSecond);
                    today = today.minusDays(-1);
                }
                Bson updateOperation = Updates.combine(
                        Updates.set("chapters_schedule", chapters_schedule),
                        Updates.set("tests_schedule", tests_schedule)
                );

                // 应用更新
                courseThreadsCollection.updateOne(eq("_id", thread.getObjectId("_id")), updateOperation);
                context.Text("{\"code\":0, \"msg\":\"Operation Succeeded!\"}");
                return;
            }
        }

        context.Text("{\"code\":-1, \"msg\":\"Not enrolled in the course!\"}");
    }

    private static void removeSchedule(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String courseId = params.get("courseId"); // The ID of the course thread
        String type = params.get("type");
        // Validate inputs
        if (!checkToken(uid, token) || courseId == null || type == null) {
            context.Text("{\"code\":-1, \"msg\":\"Incomplete parameters.\"}");
            return;
        }

        // Access the MongoDB collection for course threads
        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find the specific course thread
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        for (Document thread : threads) {
            if (thread.getInteger("courseId").toString().equals(courseId)) {
                Bson updateOperation;
                if (type.equals("0")) {
                    int tests_count = thread.getInteger("tests_count");
                    ArrayList<Integer> tests_schedule = thread.get("tests_schedule", ArrayList.class);
                    if(thread.getInteger("tests_progress") == tests_schedule.size()) {
                        context.Text("{\"code\":-1, \"msg\":\"Already finished this Activity\"}");
                        return;
                    }
                    if (tests_schedule.size() == 0) {
                        context.Text("{\"code\":-1, \"msg\":\"Operation Failed!\"}");
                        return;
                    } else {
                        tests_schedule.remove(tests_schedule.size() - 1);
                        updateOperation = Updates.set("tests_schedule", tests_schedule);
                    }
                } else {
                    int chapters_count = thread.getInteger("chapters_count");
                    ArrayList<Integer> chapters_schedule = thread.get("chapters_schedule", ArrayList.class);
                    if(thread.getInteger("chapters_progress") == chapters_schedule.size()) {
                        context.Text("{\"code\":-1, \"msg\":\"Already finished this Activity\"}");
                        return;
                    }
                    if (chapters_schedule.size() == 0) {
                        context.Text("{\"code\":-1, \"msg\":\"Operation Failed!\"}");
                        return;
                    } else {
                        chapters_schedule.remove(chapters_schedule.size() - 1);
                        updateOperation = Updates.set("chapters_schedule", chapters_schedule);
                    }
                }
                courseThreadsCollection.updateOne(eq("_id", thread.getObjectId("_id")), updateOperation);
                context.Text("{\"code\":0, \"msg\":\"Operation Succeeded!\"}");
                return;
            }
        }
        context.Text("{\"code\":-1, \"msg\":\"Not enrolled in the course!\"}");
    }

    private static void getSchedules(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }


        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find schedules for the current day
        ArrayList<Document> schedulesForToday = new ArrayList<>();
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        ArrayList<String> tests = new ArrayList<>();
        ArrayList<String> chapters = new ArrayList<>();
        for (Document thread : threads) {
            ArrayList<Integer> chaptersSchedule = thread.get("chapters_schedule", ArrayList.class);
            ArrayList<Integer> testsSchedule = thread.get("tests_schedule", ArrayList.class);
            int course_id = thread.getInteger("courseId");
            Course course = Courses.getCourse(course_id);

            // Check if any schedules fall within today's date
            int idx = 0;
            for (Integer scheduleTimestamp : chaptersSchedule) {
                schedulesForToday.add(thread);
                chapters.add("{\"name\":\"" + course.chapters.get(idx).title + "\",\"timestamp\":\"" + String.valueOf(scheduleTimestamp) + "\",\"courseId\":\"" + String.valueOf(course_id) + "\",\"courseName\":\"" + Courses.getCourse(course_id).courseName + "\"}");
                idx++;
            }

            idx = 0;
            for (Integer scheduleTimestamp : testsSchedule) {
                schedulesForToday.add(thread);
                tests.add("{\"name\":\"" + course.exams.get(idx).examTitle + "\",\"timestamp\":\"" + String.valueOf(scheduleTimestamp) + "\",\"courseId\":\"" + String.valueOf(course_id) + "\",\"courseName\":\"" + Courses.getCourse(course_id).courseName + "\"}");
                idx++;
            }
        }

        context.Text("{\"code\":0, \"tests_schedule\":" + convertToJSONArray(tests, "") + ", \"chapters_schedule\":" + convertToJSONArray(chapters, "") + "}");
    }


    private static void getSchedulesByTimestamp(Context context) {
        Map<String, String> params = context.getParams();
        String uid = params.get("uid");
        String token = params.get("token");
        String scheduled_time = params.get("scheduleTimestamp");
        long epochLong = Long.parseLong(scheduled_time) * 1000;

        ZoneId eastEightZoneId = ZoneId.of("Asia/Shanghai");
        Instant instant = Instant.ofEpochMilli(epochLong);
        LocalDate timestamp_today = instant.atZone(eastEightZoneId).toLocalDate();


        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        long startOfDayTimestamp = timestamp_today.atStartOfDay(eastEightZoneId).toEpochSecond();
        long endOfDayTimestamp = timestamp_today.plusDays(1).atStartOfDay(eastEightZoneId).toEpochSecond();


        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find schedules for the current day
        ArrayList<Document> schedulesForToday = new ArrayList<>();
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        ArrayList<String> tests = new ArrayList<>();
        ArrayList<String> chapters = new ArrayList<>();
        for (Document thread : threads) {
            ArrayList<Integer> chaptersSchedule = thread.get("chapters_schedule", ArrayList.class);
            ArrayList<Integer> testsSchedule = thread.get("tests_schedule", ArrayList.class);
            int course_id = thread.getInteger("courseId");
            Course course = Courses.getCourse(course_id);

            // Check if any schedules fall within today's date
            int idx = 0;
            for (Integer scheduleTimestamp : chaptersSchedule) {
                if(idx < thread.getInteger("chapters_progress")) {
                    idx++;
                    continue;
                }
                if (scheduleTimestamp >= startOfDayTimestamp && scheduleTimestamp < endOfDayTimestamp) {
                    schedulesForToday.add(thread);
                    chapters.add("{\"name\":\"" + course.chapters.get(idx).title + "\",\"timestamp\":\"" + String.valueOf(scheduleTimestamp) + "\",\"courseId\":\"" + String.valueOf(course_id) + "\",\"courseName\":\"" + Courses.getCourse(course_id).courseName + "\"}");
                }
                idx++;
            }

            idx = 0;
            for (Integer scheduleTimestamp : testsSchedule) {
                if (scheduleTimestamp >= startOfDayTimestamp && scheduleTimestamp < endOfDayTimestamp) {
                    if(idx < thread.getInteger("tests_progress")) {
                        idx++;
                        continue;
                    }
                    schedulesForToday.add(thread);
                    tests.add("{\"name\":\"" + course.exams.get(idx).examTitle + "\",\"timestamp\":\"" + String.valueOf(scheduleTimestamp) + "\",\"courseId\":\"" + String.valueOf(course_id) + "\",\"courseName\":\"" + Courses.getCourse(course_id).courseName + "\"}");
                }
                idx++;
            }
        }

        context.Text("{\"code\":0, \"tests_schedule\":" + convertToJSONArray(tests, "") + ", \"chapters_schedule\":" + convertToJSONArray(chapters, "") + "}");
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

    private static void getCourseContent(Context context) {
        Map<String, String> params = context.getParams();
        String courseId = params.get("courseId");
        String uid = params.get("uid");
        String token = params.get("token");
        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }
        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find schedules for the current day
        ArrayList<Document> schedulesForToday = new ArrayList<>();
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        for (Document thread : threads) {
            if(String.valueOf(thread.getInteger("courseId")).equals(courseId)) {
                int progress = thread.getInteger("chapters_progress");
                Course course = Courses.getCourse(Integer.parseInt(courseId));
                Course.Chapter chapter = course.chapters.get(progress);
                context.Text(chapter.toJSON());
            }
        }
        context.Text("{\"code\":-1, \"msg\":\"Not enrolled in this course.\"}");

    }

    private static void getTestContent(Context context) {
        Map<String, String> params = context.getParams();
        String courseId = params.get("courseId");
        String uid = params.get("uid");
        String token = params.get("token");
        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find schedules for the current day
        FindIterable<Document> threads = courseThreadsCollection.find(eq("uid", uid));
        for (Document thread : threads) {
            if(String.valueOf(thread.getInteger("courseId")).equals(courseId)) {
                int progress = thread.getInteger("tests_progress");
                Course course = Courses.getCourse(Integer.parseInt(courseId));
                Course.Exam exam = course.exams.get(progress);
                context.Text(exam.toJSON());
            }
        }
        context.Text("{\"code\":-1, \"msg\":\"Not enrolled in this course.\"}");
    }

    private static void makeProgress(Context context) {
        Map<String, String> params = context.getParams();
        int courseId = Integer.parseInt(params.get("courseId"));
        String type = params.get("type"); // 0 for tests, 1 for chapters
        String uid = params.get("uid");
        String token = params.get("token");

        // Validate input
        if (!checkToken(uid, token)) {
            context.Text("{\"code\":-1, \"msg\":\"Invalid or missing token.\"}");
            return;
        }

        MongoCollection<Document> courseThreadsCollection = Database.getCollection("lingvosphere", "courseThreads");

        // Find the course thread for the user
        Document courseThread = courseThreadsCollection.find(and(eq("uid", uid), eq("courseId", courseId))).first();

        if (courseThread != null) {
            // Update progress based on the type
            if ("0".equals(type)) {
                // Update tests progress
                int currentTestProgress = courseThread.getInteger("tests_progress");
                int totalTests = courseThread.getInteger("tests_count");
                if (currentTestProgress < totalTests) {
                    courseThreadsCollection.updateOne(eq("_id", courseThread.getObjectId("_id")), new Document("$inc", new Document("tests_progress", 1)));
                    if(totalTests == currentTestProgress + 1)
                        context.Text("{\"code\":1, \"msg\":\"Test progress updated successfully.\"}");
                    else context.Text("{\"code\":0, \"msg\":\"Test progress updated successfully.\"}");
                } else {
                    context.Text("{\"code\":-1, \"msg\":\"Test progress is already at maximum.\"}");
                }
            } else if ("1".equals(type)) {
                // Update chapters progress
                int currentChapterProgress = courseThread.getInteger("chapters_progress");
                int totalChapters = courseThread.getInteger("chapters_count");
                if (currentChapterProgress < totalChapters) {
                    courseThreadsCollection.updateOne(eq("_id", courseThread.getObjectId("_id")), new Document("$inc", new Document("chapters_progress", 1)));
                    context.Text("{\"code\":0, \"msg\":\"Chapter progress updated successfully.\"}");
                } else {
                    context.Text("{\"code\":-1, \"msg\":\"Chapter progress is already at maximum.\"}");
                }
            } else {
                context.Text("{\"code\":-1, \"msg\":\"Invalid progress type specified.\"}");
            }
        } else {
            context.Text("{\"code\":-1, \"msg\":\"Course thread not found.\"}");
        }
    }
}
