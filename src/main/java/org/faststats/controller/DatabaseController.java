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

    public @Nullable JsonObject createProject(String ownerId, String projectName, String slug, boolean isPrivate) {
        var projects = database.getCollection("projects");

        var document1 = new Document("slug", slug);
        if (projects.find(document1).limit(1).first() != null) return null;

        var document2 = new Document("projectName", projectName).append("ownerId", ownerId);
        if (projects.find(document2).limit(1).first() != null) return null;

        var first = projects.find().sort(new Document("projectId", -1)).limit(1).first();
        var id = first != null ? first.getInteger("projectId") + 1 : 1;

        var result = projects.insertOne(new Document("slug", slug)
                .append("projectName", projectName)
                .append("private", isPrivate)
                .append("ownerId", ownerId));
        if (!result.wasAcknowledged()) return null;

        var project = new JsonObject();
        project.addProperty("private", isPrivate);
        project.addProperty("projectId", id);
        project.addProperty("projectName", projectName);
        project.addProperty("slug", slug);
        project.addProperty("ownerId", ownerId);
        return project;
    }

    public boolean deleteProject(int projectId, @Nullable String ownerId) {
        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);
        if (ownerId != null) filter.append("ownerId", ownerId);
        return projects.deleteOne(filter).getDeletedCount() > 0;
    }

    public List<JsonObject> getProjects(int offset, int limit, @Nullable String ownerId, @Nullable Boolean publicOnly) {
        var filter = new Document();
        if (ownerId != null) filter.append("ownerId", ownerId);
        if (publicOnly != null) filter.append("private", !publicOnly);
        var projects = database.getCollection("projects");
        return projects.find(filter).skip(offset).limit(limit)
                .map(this::getProject).into(new ArrayList<>());
    }

    public int renameProject(int projectId, String projectName, @Nullable String ownerId) {
        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);
        if (ownerId != null) filter.append("ownerId", ownerId);

        var project = projects.find(filter).limit(1).first();
        if (project == null) return 404;

        var duplicateOwnerId = project.getString("ownerId");
        var duplicate = new Document("ownerId", duplicateOwnerId).append("projectName", projectName);
        if (projects.find(duplicate).limit(1).first() != null) return 409;

        var update = new Document("$set", new Document("projectName", projectName));
        var result = projects.updateOne(filter, update);
        return result.getModifiedCount() > 0 ? 204 : 304;
    }

    public int updateProject(int projectId, @Nullable ProjectSettings settings, @Nullable String ownerId) {
        if (settings == null || settings.isEmpty()) return 304;
        if (!settings.isValid()) return 400;

        var projects = database.getCollection("projects");
        var filter = new Document("projectId", projectId);
        if (ownerId != null) filter.append("ownerId", ownerId);

        var project = projects.find(filter).limit(1).first();
        if (project == null) return 404;

        if (settings.previewChart() != null && settings.layout() == null) {
            if (!project.containsKey("layout")) return 400;
            var layout = project.get("layout", Document.class);
            if (!layout.containsKey(settings.previewChart())) return 400;
        }

        var result = projects.updateOne(filter, new Document("$set", settings.toDocument()));
        return result.getModifiedCount() > 0 ? 204 : 304;
    }

    public boolean isSlugUsed(String slug) {
        var projects = database.getCollection("projects");
        return projects.find(new Document("slug", slug)).limit(1).first() != null;
    }

    public @Nullable JsonObject getProject(String slug, @Nullable String ownerId) {
        var projects = database.getCollection("projects");
        var document = projects.find(new Document("slug", slug)).first();
        if (document == null) return null;

        var project = getProject(document);
        if (project.has("private") && project.get("private").getAsBoolean()) {
            var owner = project.get("ownerId").getAsString();
            if (ownerId == null || !ownerId.equals(owner)) return null;
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
        project.addProperty("slug", document.getString("slug"));
        project.addProperty("ownerId", document.getString("ownerId"));
        if (document.containsKey("preview_chart"))
            project.addProperty("preview_chart", document.getString("preview_chart"));
        if (document.containsKey("icon")) project.addProperty("icon", document.getString("icon"));
        if (document.containsKey("project_url")) project.addProperty("project_url", document.getString("project_url"));
        return project;
    }
}
