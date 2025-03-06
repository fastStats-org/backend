package org.faststats.route;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RouteHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteHandler.class);

    public static Handler async(Handle handle) {
        return context -> context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                handle.handle(context);
            } catch (SQLException e) {
                LOGGER.error("An error occurred while performing an SQL statement", e);
                context.result(e.getMessage()).status(500);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.result(throwable.getMessage()).status(500);
            return null;
        }));
    }

    public interface Handle {
        void handle(Context context) throws SQLException;
    }
}
