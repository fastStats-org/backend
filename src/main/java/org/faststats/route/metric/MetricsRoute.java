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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private static final int MAX_REQUESTS_PER_IP = 10;

    private static final Map<String, RequestTracker> requestCounts = new ConcurrentHashMap<>();

    private static class RequestTracker {
        private int count;
        private final long timestamp;

        public RequestTracker(int count) {
            this.count = count;
            this.timestamp = System.currentTimeMillis();
        }

        public int incrementAndGet() {
            return ++count;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 60000; // 60000ms = 1 minute
        }
    }

    private static boolean isRequestLimitExceeded(String address) {
        return requestCounts.compute(address, (key, tracker) -> {
            if (tracker == null || tracker.isExpired()) {
                tracker = new RequestTracker(0);
            }
            int newCount = tracker.incrementAndGet();

            return tracker;
        }).count > MAX_REQUESTS_PER_IP;
    }
}
