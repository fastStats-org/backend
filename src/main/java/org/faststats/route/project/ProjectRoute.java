package org.faststats.route.project;

import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class ProjectRoute {
    private final FastStats fastStats;

    public ProjectRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().get("/project/{projectId}", this::get);
    }

    private void get(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var userId = context.queryParam("userId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var project = fastStats.database().getProject(projectId, userId);

                if (project != null) {
                    context.header("Content-Type", "application/json");
                    context.result(project.toString());
                    context.status(200);
                } else {
                    context.status(404);
                }
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
