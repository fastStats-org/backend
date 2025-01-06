package org.faststats.controller;

import com.google.gson.JsonObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.faststats.FastStats;
import org.faststats.model.ProjectSettings;
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

        var projects = database.getCollection("projects");
        projects.createIndex(new Document("projectId", 1), new IndexOptions().unique(true));

        logger.info("Successfully connected to MongoDB!");
    }

    public @Nullable JsonObject createProject(String userId, String projectName) {
        var projects = database.getCollection("projects");
        var first = projects.find().sort(new Document("projectId", -1)).limit(1).first();
        var id = first != null ? first.getInteger("projectId") + 1 : 1;

        var document = new Document("projectName", projectName).append("userId", userId);
        if (projects.find(document).first() != null) return null;

        var result = projects.insertOne(document.append("projectId", id));
        if (!result.wasAcknowledged()) return null;

        var project = new JsonObject();
        project.addProperty("private", false);
        project.addProperty("projectId", id);
        project.addProperty("projectName", projectName);
        project.addProperty("userId", userId);
        return project;
    }

    public boolean deleteProject(int projectId) {
        var projects = database.getCollection("projects");
        var project = new Document("projectId", projectId);
        return projects.deleteOne(project).getDeletedCount() > 0;
    }

    public List<JsonObject> getProjects(int offset, int limit, @Nullable String userId) {
        var filter = new Document();
        if (userId != null) filter.append("userId", userId);
        var projects = database.getCollection("projects");

        return projects.find(filter).skip(offset).limit(limit).map(document -> {
            var project = new JsonObject();
            project.addProperty("private", document.getBoolean("private", false));
            project.addProperty("projectId", document.getInteger("projectId"));
            project.addProperty("projectName", document.getString("projectName"));
            project.addProperty("userId", document.getString("userId"));
            return project;
        }).into(new ArrayList<>());
    }

    public int renameProject(int projectId, String projectName) {
        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);

        var project = projects.find(filter).first();
        if (project == null) return 404;

        var userId = project.getString("userId");
        var duplicate = new Document("userId", userId).append("projectName", projectName);
        if (projects.find(duplicate).first() != null) return 409;

        var update = new Document("$set", new Document("projectName", projectName));
        var result = projects.updateOne(filter, update);
        return result.getModifiedCount() > 0 ? 200 : 304;
    }

    public int updateProject(int projectId, ProjectSettings settings) {
        if (settings.isEmpty()) return 304;

        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);

        var project = projects.find(filter).first();
        if (project == null) return 404;

        var update = new Document();

        if (settings.isPrivate() != null) update.append("private", settings.isPrivate());

        var result = projects.updateOne(filter, new Document("$set", update));
        return result.getModifiedCount() > 0 ? 200 : 304;
    }
}
