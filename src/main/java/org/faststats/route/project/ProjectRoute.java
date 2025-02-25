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
public class ProjectRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRoute.class);

    public static void register(Javalin javalin) {
        javalin.get("/project/{slug}", ProjectRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var slug = context.pathParam("slug");
                var project = FastStats.DATABASE.getProject(slug, ownerId);

                if (project != null) {
                    context.header("Content-Type", "application/json");
                    context.result(project.toString());
                    context.status(200);
                } else {
                    context.status(404);
                }
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
