package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class RenameRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/rename/{projectId}/{projectName}", RenameRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var projectName = context.pathParam("projectName");
                context.status(FastStats.DATABASE.renameProject(projectId, projectName, ownerId));
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
