package org.faststats.route;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RouteHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteHandler.class);

    public static Handler async(Consumer<Context> consumer) {
        return context -> context.future(() -> CompletableFuture.runAsync(() -> consumer.accept(context))
                .orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
                    LOGGER.error("Failed to handle request", throwable);
                    context.result(throwable.getMessage()).status(500);
                    return null;
                }));
    }
}
