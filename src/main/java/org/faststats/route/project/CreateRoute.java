package org.faststats.route.project;

import io.javalin.http.Context;
import org.faststats.FastStats;

import java.util.concurrent.CompletableFuture;

public class CreateRoute {
    private final FastStats fastStats;

    public CreateRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().post("/projects/new/{userId}/{projectName}", this::create);
    }

    private void create(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            var userId = context.pathParam("userId");
            var projectName = context.pathParam("projectName");
            var project = fastStats.database().createProject(userId, projectName);

            if (project == null) {
                context.status(409);
            } else {
                context.header("Content-Type", "application/json");
                context.result(project.toString());
                context.status(200);
            }
        }));
    }
}
