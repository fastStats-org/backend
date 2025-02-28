package org.faststats.route.project.settings;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class SlugSettingsRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/slug/{projectId}/{slug}", async(SlugSettingsRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var slug = context.pathParam("slug");
            var updated = FastStats.DATABASE.updateSlug(projectId, slug, ownerId);
            context.status(updated ? 204 : 304);
        } catch (IllegalArgumentException | SQLException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
