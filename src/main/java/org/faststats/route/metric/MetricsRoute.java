package org.faststats.route.metric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.model.Metric;
import org.jspecify.annotations.NullMarked;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import static org.faststats.route.RouteHandler.async;
import static org.faststats.route.RouteHandler.error;

@NullMarked
public class MetricsRoute {
    public static void register(Javalin javalin) {
        javalin.post("/metrics", async(MetricsRoute::handle));
        javalin.options("/metrics", context -> {
            context.header("Access-Control-Allow-Headers", "Content-Type, Content-Encoding");
            context.header("Access-Control-Allow-Methods", "POST");
            context.header("Access-Control-Allow-Origin", "*");
            context.status(204);
        });
    }

    private static void handle(Context context) {
        try {
            var data = decompressData(context.bodyAsBytes());
            var metric = Metric.fromJson(data);
            //FastStats.DATABASE.insertMetric(metric);
            context.status(200);
        } catch (IOException | IllegalStateException e) {
            error(context, e, 400);
        }
    }

    private static JsonObject decompressData(byte[] data) throws IOException {
        try (var input = new GZIPInputStream(new ByteArrayInputStream(data))) {
            var decompressed = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(decompressed).getAsJsonObject();
        }
    }
}
