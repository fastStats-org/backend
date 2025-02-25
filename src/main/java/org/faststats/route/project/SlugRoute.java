package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SlugRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlugRoute.class);

    public static void register(Javalin javalin) {
        javalin.head("/project/slug/{slug}", SlugRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var slug = context.pathParam("slug");
                context.status(FastStats.DATABASE.isSlugUsed(slug) ? 409 : 204);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.status(500).result(throwable.getMessage());
            return null;
        }));
    }
}
