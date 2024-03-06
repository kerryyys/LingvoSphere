package com.lingvosphere.backend;

import com.lingvosphere.backend.Interfaces.*;
import com.lingvosphere.backend.Models.Course;
import com.lingvosphere.backend.Utils.CourseReader;

import xyz.Blockers.Utils.RestfulAPIServer.APIHandler;
import xyz.Blockers.Utils.RestfulAPIServer.Context;
import xyz.Blockers.Utils.RestfulAPIServer.RestfulAPIServer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    public static void main(String[] args){
        File[] CourseFiles = CourseReader.getJSONFiles("./Data");
        for(File courseFile : CourseFiles) {
            Course course = CourseReader.read("./Data/" + courseFile.getName());
            Courses.courses.add(course);
        }
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);
        Database.init("127.0.0.1", 27017);
        RestfulAPIServer server;
        try {
            server = new RestfulAPIServer(1024);
        } catch (IOException e) {
            System.out.println("Failed Starting server :(");
            System.out.println("\tCause:");
            System.out.println("\t\t" + e.getMessage());
            return;
        }
        server.setPath("/web");

        server.GET("/", new APIHandler() {
            @Override
            public void handle(Context context) {
                context.Text("Server started!");
            }
        });

        UserInterface.init(server);
        AccoladeOasisInterface.init(server);
        LearningScheduleInterface.init(server);
        MentorshipHubInterface.init(server);
        WebpageInterface.init(server);

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
        }));

    }
}
