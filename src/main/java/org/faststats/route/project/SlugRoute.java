package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SlugRoute {
    public static void register(Javalin javalin) {
        javalin.head("/project/slug/{slug}", SlugRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var slug = context.pathParam("slug");
                var slugUsed = FastStats.DATABASE.isSlugUsed(slug);
                context.status(slugUsed ? 409 : 204);
            } catch (SQLException e) {
                context.result(e.getMessage());
                context.status(400);
            }
        }));
    }
}
