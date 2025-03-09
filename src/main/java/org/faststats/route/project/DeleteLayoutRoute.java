package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;
import static org.faststats.route.RouteHandler.error;

public class DeleteLayoutRoute {
    public static void register(Javalin javalin) {
        javalin.delete("/project/layout/delete/{projectId}", async(DeleteLayoutRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var deleted = FastStats.DATABASE.deleteChart(projectId, ownerId);
            context.status(deleted ? 204 : 404);
        } catch (NumberFormatException e) {
            error(context, e, 400);
        }
    }
}
