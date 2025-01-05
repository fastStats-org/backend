package org.faststats.route.project;

import io.javalin.http.Context;
import org.faststats.FastStats;

import java.util.concurrent.CompletableFuture;

public class DeleteRoute {
    private final FastStats fastStats;

    public DeleteRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().delete("/project/{projectId}", this::delete);
    }

    private void delete(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var deleted = fastStats.database().deleteProject(projectId);
                context.status(deleted ? 200 : 404);
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
