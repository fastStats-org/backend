package org.faststats.route.project;

import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class DeleteRoute {
    private final FastStats fastStats;

    public DeleteRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().delete("/projects/{userId}/{projectId}", this::delete);
    }

    private void delete(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var userId =  context.queryParam("userId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var deleted = fastStats.database().deleteProject(projectId, userId);
                context.status(deleted ? 200 : 404);
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
