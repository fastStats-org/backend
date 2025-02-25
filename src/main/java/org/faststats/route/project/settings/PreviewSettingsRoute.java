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
public class PreviewSettingsRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewSettingsRoute.class);

    public static void register(Javalin javalin) {
        javalin.put("/project/settings/preview/{projectId}/{chart}", PreviewSettingsRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var chart = context.pathParam("chart");
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var updated = FastStats.DATABASE.updatePreviewChart(projectId, chart.isBlank() ? null : chart, ownerId);
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
