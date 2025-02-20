package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;

import java.util.concurrent.CompletableFuture;

public class SlugRoute {
    public static void register(Javalin javalin) {
        javalin.head("/project/slug/{slug}", SlugRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            var slug = context.pathParam("slug");
            var slugUsed = FastStats.DATABASE.isSlugUsed(slug);
            context.status(slugUsed ? 409 : 204);
        }));
    }
}
