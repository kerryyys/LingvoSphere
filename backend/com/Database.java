package com.lingvosphere.backend;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

public class Database {
    private static MongoClient mongoClient;

    // Initialize the MongoDB connection
    public static void init(String host, int port) {
        if (mongoClient == null) {
            mongoClient = new MongoClient(host, port);
        }
    }

    // Get a database instance
    public static MongoDatabase getDatabase(String databaseName) {
        if (mongoClient == null) {
            throw new IllegalStateException("Database connection has not been initialized. Call init() first.");
        }
        return mongoClient.getDatabase(databaseName);
    }

    // Get a collection instance from a database
    public static MongoCollection getCollection(String databaseName, String collectionName) {
        MongoDatabase database = getDatabase(databaseName);
        return database.getCollection(collectionName);
    }

    // Close the database connection
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}

