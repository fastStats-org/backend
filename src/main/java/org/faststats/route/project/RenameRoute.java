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
public class RenameRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(RenameRoute.class);

    public static void register(Javalin javalin) {
        javalin.put("/project/rename/{projectId}/{name}", RenameRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var name = context.pathParam("name");
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var renamed = FastStats.DATABASE.renameProject(projectId, name, ownerId);
                context.status(renamed ? 204 : 409);
            } catch (NumberFormatException e) {
                context.result(e.getMessage());
                context.status(400);
            } catch (SQLException e) {
                context.result(e.getMessage());
                context.status(409);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.status(500).result(throwable.getMessage());
            return null;
        }));
    }
}
