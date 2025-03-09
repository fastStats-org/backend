package org.faststats.route;

import com.google.gson.JsonObject;
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
                error(context, e, 500);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            error(context, throwable, 500);
            return null;
        }));
    }

    public static void error(Context context, Throwable throwable, int status) {
        var result = new JsonObject();
        result.addProperty("error", throwable.getMessage());
        result.addProperty("body", context.body());
        context.header("Content-Type", "application/json");
        context.result(result.toString()).status(status);
    }

    public interface Handle {
        void handle(Context context) throws SQLException;
    }
}
