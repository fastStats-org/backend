package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class ProjectRoute {
    public static void register(Javalin javalin) {
        javalin.get("/project/{slug}", ProjectRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var slug = context.pathParam("slug");
                var project = FastStats.DATABASE.getProject(slug, ownerId);

                if (project != null) {
                    context.header("Content-Type", "application/json");
                    context.result(project.toString());
                    context.status(200);
                } else {
                    context.status(404);
                }
            } catch (NumberFormatException | SQLException e) {
                context.result(e.getMessage());
                context.status(400);
            }
        }));
    }
}
