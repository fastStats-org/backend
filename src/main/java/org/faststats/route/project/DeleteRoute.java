package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class DeleteRoute {
    public static void register(Javalin javalin) {
        javalin.delete("/project/delete/{projectId}", DeleteRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var deleted = FastStats.DATABASE.deleteProject(projectId, ownerId);
                context.status(deleted ? 204 : 404);
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
