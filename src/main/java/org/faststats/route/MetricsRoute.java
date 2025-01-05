package org.faststats.route;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@NullMarked
public class MetricsRoute {
    private static final Logger logger = LoggerFactory.getLogger(MetricsRoute.class);

    private final FastStats fastStats;

    public MetricsRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().post("/metrics", this::post);
        fastStats.javalin().options("/metrics", context -> {
            context.header("Access-Control-Allow-Headers", "Content-Type, Content-Encoding");
            context.header("Access-Control-Allow-Methods", "POST");
            context.header("Access-Control-Allow-Origin", "*");
            context.status(204);
        });
    }

    private void post(Context context) {
        context.future(() -> {
            try {
                var data = decompressData(context.bodyAsBytes());
                var projectId = data.get("projectId").getAsInt();
                var javaVersion = data.get("javaVersion").getAsString();
                var osName = data.get("osName").getAsString();
                var osArch = data.get("osArch").getAsString();
                var osVersion = data.get("osVersion").getAsString();
                var processors = data.get("processors").getAsInt();
                var charts = data.get("charts").getAsJsonArray();

                context.status(200);
                return null;
            } catch (Exception e) {
                logger.error("Received invalid metrics data", e);
                context.status(400);
                return null;
            }
        });
    }

    private JsonObject decompressData(byte[] data) throws IOException {
        try (var input = new GZIPInputStream(new ByteArrayInputStream(data))) {
            var decompressed = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(decompressed).getAsJsonObject();
        }
    }
}
