package org.faststats.route.project;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProjectsRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectsRoute.class);

    public static void register(Javalin javalin) {
        javalin.get("/projects/count/", ProjectsRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var object = new JsonObject();
                object.addProperty("projects", FastStats.DATABASE.countProjects(ownerId));
                context.header("Content-Type", "application/json");
                context.result(object.toString());
                context.status(200);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.status(500).result(throwable.getMessage());
            return null;
        }));
    }
}
