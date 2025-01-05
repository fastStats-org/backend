package org.faststats.controller;

import com.google.gson.JsonObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class DatabaseController {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);
    private final MongoDatabase database;

    @SuppressWarnings("resource")
    public DatabaseController(FastStats fastStats) {
        var server = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        var connectionString = new ConnectionString(
                fastStats.config().connectionString()
        );

        var settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(server)
                .build();

        var client = MongoClients.create(settings);
        this.database = client.getDatabase("fastStats");

        database.createCollection("projects");
        database.createCollection("consumers");

        logger.info("Successfully connected to MongoDB!");
    }

    public @Nullable JsonObject createProject(String userId, String projectName) {
        var projects = database.getCollection("projects");
        var id = (int) projects.countDocuments() + 1;

        var document = new Document("projectName", projectName).append("userId", userId);
        if (projects.find(document).first() != null) return null;

        var result = projects.insertOne(document.append("projectId", id));
        if (!result.wasAcknowledged()) return null;

        var project = new JsonObject();
        project.addProperty("projectId", id);
        project.addProperty("projectName", projectName);
        project.addProperty("userId", userId);
        return project;
    }

    public boolean deleteProject(int projectId) {
        var projects = database.getCollection("projects");
        var project = new Document("projectId", projectId);
        return projects.deleteMany(project).getDeletedCount() > 0;
    }

    public List<JsonObject> getProjects(int offset, int limit, @Nullable String userId) {
        var filter = new Document();
        if (userId != null) filter.append("userId", userId);
        var projects = database.getCollection("projects");
        return projects.find(filter).skip(offset).limit(limit).map(document -> {
            var project = new JsonObject();
            project.addProperty("projectId", document.getString("projectId"));
            project.addProperty("projectName", document.getString("projectName"));
            project.addProperty("userId", userId);
            return project;
        }).into(new ArrayList<>());
    }
}
