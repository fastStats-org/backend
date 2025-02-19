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
    public DatabaseController() {
        var server = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        var env = System.getenv("MONGODB_URL");
        var url = env != null ? env : FastStats.CONFIG.connectionString();
        var connectionString = new ConnectionString(url);

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

    public @Nullable JsonObject createProject(String userId, String projectName, boolean isPrivate) {
        var projects = database.getCollection("projects");
        var first = projects.find().sort(new Document("projectId", -1)).limit(1).first();
        var id = first != null ? first.getInteger("projectId") + 1 : 1;

        var document = new Document("projectName", projectName).append("userId", userId);
        if (projects.find(document).first() != null) return null;

        var result = projects.insertOne(document.append("projectId", id));
        if (!result.wasAcknowledged()) return null;

        var project = new JsonObject();
        project.addProperty("private", isPrivate);
        project.addProperty("projectId", id);
        project.addProperty("projectName", projectName);
        project.addProperty("userId", userId);
        return project;
    }

    public boolean deleteProject(int projectId, @Nullable String userId) {
        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);
        if (userId != null) filter.append("userId", userId);
        return projects.deleteOne(filter).getDeletedCount() > 0;
    }

    public List<JsonObject> getProjects(int offset, int limit, @Nullable String userId, @Nullable Boolean publicOnly) {
        var filter = new Document();
        if (userId != null) filter.append("userId", userId);
        if (publicOnly != null) filter.append("private", !publicOnly);
        var projects = database.getCollection("projects");
        return projects.find(filter).skip(offset).limit(limit)
                .map(this::getProject).into(new ArrayList<>());
    }

    public int renameProject(int projectId, String projectName, @Nullable String userId) {
        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);
        if (userId != null) filter.append("userId", userId);

        var project = projects.find(filter).first();
        if (project == null) return 404;

        var duplicateUserId = project.getString("userId");
        var duplicate = new Document("userId", duplicateUserId).append("projectName", projectName);
        if (projects.find(duplicate).first() != null) return 409;

        var update = new Document("$set", new Document("projectName", projectName));
        var result = projects.updateOne(filter, update);
        return result.getModifiedCount() > 0 ? 200 : 304;
    }

    public int updateProject(int projectId, @Nullable ProjectSettings settings, @Nullable String userId) {
        if (settings == null || settings.isEmpty()) return 304;
        if (!settings.isValid()) return 400;

        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);
        if (userId != null) filter.append("userId", userId);

        var project = projects.find(filter).first();
        if (project == null) return 404;

        if (settings.previewChart() != null && settings.layout() == null) {
            if (!project.containsKey("layout")) return 400;
            var layout = project.get("layout", Document.class);
            if (!layout.containsKey(settings.previewChart())) return 400;
        }

        var update = new Document();

        if (settings.isPrivate() != null) update.append("private", settings.isPrivate());
        if (settings.layout() != null) update.append("layout", settings.layout().toDocument());
        if (settings.previewChart() != null) update.append("preview_chart", settings.previewChart());
        if (settings.projectUrl() != null) update.append("project_url", settings.projectUrl());
        if (settings.icon() != null) update.append("icon", settings.icon());

        var result = projects.updateOne(filter, new Document("$set", update));
        return result.getModifiedCount() > 0 ? 200 : 304;
    }

    public @Nullable JsonObject getProject(int projectId, @Nullable String userId) {
        var projects = database.getCollection("projects");
        var document = projects.find(new Document("projectId", projectId)).first();
        if (document == null) return null;

        var project = getProject(document);
        if (project.has("private") && project.get("private").getAsBoolean()) {
            var owner = project.get("userId").getAsString();
            if (userId == null || !userId.equals(owner)) return null;
        }

        if (document.containsKey("layout")) project.add("layout",
                getLayout(document.get("layout", Document.class)));
        return project;
    }

    private JsonObject getLayout(Document document) {
        var layout = new JsonObject();
        document.keySet().forEach(chartId -> {
            var chart = new JsonObject();
            var options = document.get(chartId, Document.class);
            chart.addProperty("name", options.getString("name"));
            chart.addProperty("type", options.getString("type"));
            chart.addProperty("color", options.getString("color"));
            if (options.containsKey("icon")) chart.addProperty("icon", options.getString("icon"));
            if (options.containsKey("size")) chart.addProperty("size", options.getInteger("size"));
            layout.add(chartId, chart);
        });
        return layout;
    }

    private JsonObject getProject(Document document) {
        var project = new JsonObject();
        project.addProperty("private", document.getBoolean("private", false));
        project.addProperty("projectId", document.getInteger("projectId"));
        project.addProperty("projectName", document.getString("projectName"));
        project.addProperty("userId", document.getString("userId"));
        if (document.containsKey("preview_chart"))
            project.addProperty("preview_chart", document.getString("preview_chart"));
        if (document.containsKey("icon")) project.addProperty("icon", document.getString("icon"));
        if (document.containsKey("project_url")) project.addProperty("project_url", document.getString("project_url"));
        return project;
    }
}
