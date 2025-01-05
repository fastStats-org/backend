package org.faststats.controller;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.faststats.FastStats;
import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public @Nullable Project createProject(String userId, String projectName) {
        var projects = database.getCollection("projects");
        var id = (int) projects.countDocuments() + 1;

        var project = new Document("projectName", projectName).append("userId", userId);
        if (projects.find(project).first() != null) return null;

        var result = projects.insertOne(project.append("projectId", id));
        if (!result.wasAcknowledged()) return null;

        return new Project(projectName, userId, id);
    }

    public boolean deleteProject(int projectId) {
        var projects = database.getCollection("projects");
        var project = new Document("projectId", projectId);
        return projects.deleteMany(project).getDeletedCount() > 0;
    }
}
