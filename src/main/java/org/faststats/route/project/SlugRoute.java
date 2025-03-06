package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

public class SlugRoute {
    public static void register(Javalin javalin) {
        javalin.head("/project/slug/{slug}", async(SlugRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        var slug = context.pathParam("slug");
        context.status(FastStats.DATABASE.isSlugUsed(slug) ? 409 : 204);
    }
}
