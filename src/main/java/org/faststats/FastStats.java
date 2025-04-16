package org.faststats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.file.format.GsonFile;
import core.io.IO;
import org.faststats.controller.DatabaseController;
import org.faststats.model.Config;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

@NullMarked
public class FastStats {
    public static final Config CONFIG = new GsonFile<>(IO.of("data", "config.json"), new Config(
            3000, 5000, "", "*"
    )).validate().save().getRoot();

    public static final DatabaseController DATABASE = new DatabaseController(new File("data", "saves.db"));

    private static final APIServer API_SERVER = new APIServer();
    private static final MetricsServer METRICS_SERVER = new MetricsServer();

    public static void main(String[] args) {
        var fastStats = new FastStats();
        fastStats.registerRoutes();
        fastStats.start();
    }

    private void registerRoutes() {
        API_SERVER.registerRoutes();
        METRICS_SERVER.registerRoutes();
    }

    private void start() {
        API_SERVER.start();
        METRICS_SERVER.start();
    }

    public static <T> T nonnull(@Nullable JsonObject object, String key, Function<JsonElement, T> transformer) throws IllegalStateException {
        var element = nullable(object, key, transformer);
        if (element != null) return element;
        throw new IllegalStateException("'" + key + "' is required but not defined or null");
    }

    public static <T> @Nullable T nullable(@Nullable JsonObject object, String key, Function<JsonElement, T> transformer) {
        var element = object != null ? object.get(key) : null;
        if (element == null || element.isJsonNull()) return null;
        return transformer.apply(element);
    }
}
