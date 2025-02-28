package org.faststats.route.project.settings;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class VisibilitySettingsRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/private/{projectId}/{private}", async(VisibilitySettingsRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var isPrivate = Boolean.parseBoolean(context.pathParam("private"));
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var updated = FastStats.DATABASE.updateVisibility(projectId, isPrivate, ownerId);
            context.status(updated ? 204 : 304);
        } catch (NumberFormatException | SQLException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
