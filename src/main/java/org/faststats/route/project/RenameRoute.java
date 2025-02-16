package org.faststats.route.project;

import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class RenameRoute {
    private final FastStats fastStats;

    public RenameRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().put("/projects/{projectId}/{projectName}", this::rename);
    }

    private void rename(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var userId = context.queryParam("userId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var projectName = context.pathParam("projectName");
                context.status(fastStats.database().renameProject(projectId, projectName, userId));
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
