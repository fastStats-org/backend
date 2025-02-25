package org.faststats;

import core.file.format.GsonFile;
import core.io.IO;
import org.faststats.controller.DatabaseController;
import org.faststats.model.Config;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.Executors;

@NullMarked
public class FastStats {
    public static final Config CONFIG = new GsonFile<>(IO.of("data", "config.json"), new Config(
            3000, 5000, "", "*"
    )).validate().save().getRoot();

    public static final DatabaseController DATABASE = new DatabaseController();

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
        try (var executor = Executors.newFixedThreadPool(2)) {
            executor.submit(API_SERVER::start);
            executor.submit(METRICS_SERVER::start);
            executor.shutdown();
        }
    }
}
