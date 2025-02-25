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
public class LayoutSettingsRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutSettingsRoute.class);

    public static void register(Javalin javalin) {
        javalin.put("/project/settings/layout/rename/{projectId}/{chart}/{name}", LayoutSettingsRoute::rename);
    }

    private static void rename(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var chart = context.pathParam("chart");
                var name = context.pathParam("name");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var updated = FastStats.DATABASE.renameChart(projectId, chart, name, ownerId);
                context.status(updated ? 204 : 304);
            } catch (SQLException e) {
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
