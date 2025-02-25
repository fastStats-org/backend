package org.faststats.route.project.settings;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@NullMarked
public class VisibilitySettingsRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisibilitySettingsRoute.class);

    public static void register(Javalin javalin) {
        javalin.put("/project/settings/private/{projectId}/{private}", VisibilitySettingsRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var isPrivate = Boolean.parseBoolean(context.pathParam("private"));
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var updated = FastStats.DATABASE.updateVisibility(projectId, isPrivate, ownerId);
                context.status(updated ? 204 : 304);
            } catch (NumberFormatException | SQLException e) {
                context.result(e.getMessage());
                context.status(400);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.result(throwable.getMessage()).status(500);
            return null;
        }));
    }
}
