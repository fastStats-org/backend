package org.faststats;

import io.javalin.Javalin;
import org.faststats.route.metric.MetricsRoute;

public class MetricsServer {
    private final Javalin javalin = Javalin.create(config -> {
        config.showJavalinBanner = false;
        config.useVirtualThreads = true;
    });

    public void registerRoutes() {
        MetricsRoute.register(javalin);
    }

    public void start() {
        var env = System.getenv("METRICS_PORT");
        var port = env != null ? Integer.decode(env) : FastStats.CONFIG.metricsPort();
        javalin.start(port);
    }
}
