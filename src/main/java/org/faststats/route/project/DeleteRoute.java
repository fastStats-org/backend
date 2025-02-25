package org.faststats.route.project;

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
public class DeleteRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRoute.class);

    public static void register(Javalin javalin) {
        javalin.delete("/project/delete/{projectId}", DeleteRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var deleted = FastStats.DATABASE.deleteProject(projectId, ownerId);
                context.status(deleted ? 204 : 404);
            } catch (NumberFormatException | SQLException e) {
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
