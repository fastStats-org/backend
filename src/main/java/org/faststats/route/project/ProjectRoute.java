package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class ProjectRoute {
    public static void register(Javalin javalin) {
        javalin.get("/project/{slug}", async(ProjectRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var slug = context.pathParam("slug");
            var project = FastStats.DATABASE.getProject(slug, ownerId);

            if (project != null) {
                context.header("Content-Type", "application/json");
                context.result(project.toJson().toString());
                context.status(200);
            } else {
                context.status(404);
            }
        } catch (NumberFormatException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
