package org.faststats;

import io.javalin.Javalin;
import org.faststats.route.metric.MetricsRoute;

public class MetricsServer {
    private final Javalin javalin = Javalin.create(config -> {
        config.showJavalinBanner = false;
        config.useVirtualThreads = false;
    });

    public void registerRoutes() {
        MetricsRoute.register(javalin);
    }

    public void start() {
        javalin.start(FastStats.CONFIG.metricsPort());
    }
}
