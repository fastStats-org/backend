package org.faststats.route.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@NullMarked
public class ListRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListRoute.class);

    public static void register(Javalin javalin) {
        javalin.get("/projects/list/{offset}/{limit}", ListRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var param = context.queryParam("publicOnly");
                var publicOnly = param != null ? Boolean.parseBoolean(param) : null;
                var ownerId = context.queryParam("ownerId");

                var limit = Integer.parseInt(context.pathParam("limit"));
                var offset = Integer.parseInt(context.pathParam("offset"));

                var projects = new JsonArray();
                FastStats.DATABASE.getProjects(offset, limit, ownerId, publicOnly)
                        .stream().map(Project::toJson).forEach(projects::add);
                context.header("Content-Type", "application/json");
                context.result(projects.toString());
                context.status(200);
            } catch (IllegalStateException | JsonSyntaxException | SQLException | NumberFormatException e) {
                context.result(e.getMessage());
                context.status(400);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.status(500).result(throwable.getMessage());
            return null;
        }));
    }
}
