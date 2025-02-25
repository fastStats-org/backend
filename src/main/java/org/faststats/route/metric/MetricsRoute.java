package org.faststats.route.metric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.model.Metric;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@NullMarked
public class MetricsRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsRoute.class);

    public static void register(Javalin javalin) {
        javalin.post("/metrics", MetricsRoute::handle);
        javalin.options("/metrics", context -> {
            context.header("Access-Control-Allow-Headers", "Content-Type, Content-Encoding");
            context.header("Access-Control-Allow-Methods", "POST");
            context.header("Access-Control-Allow-Origin", "*");
            context.status(204);
        });
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var data = decompressData(context.bodyAsBytes());
                Metric.fromJson(data);

                context.status(200);
            } catch (IOException | IllegalStateException e) {
                context.status(400);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.result(throwable.getMessage()).status(500);
            return null;
        }));
    }

    private static JsonObject decompressData(byte[] data) throws IOException {
        try (var input = new GZIPInputStream(new ByteArrayInputStream(data))) {
            var decompressed = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(decompressed).getAsJsonObject();
        }
    }
}
