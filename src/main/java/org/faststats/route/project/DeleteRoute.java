package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;
import static org.faststats.route.RouteHandler.error;

@NullMarked
public class DeleteRoute {
    public static void register(Javalin javalin) {
        javalin.delete("/project/delete/{projectId}", async(DeleteRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var deleted = FastStats.DATABASE.deleteProject(projectId, ownerId);
            context.status(deleted ? 204 : 404);
        } catch (NumberFormatException e) {
            error(context, e, 400);
        }
    }
}
