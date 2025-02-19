package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class ProjectRoute {
    public static void register(Javalin javalin) {
        javalin.get("/project/{projectId}", ProjectRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var userId = context.queryParam("userId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var project = FastStats.DATABASE.getProject(projectId, userId);

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
