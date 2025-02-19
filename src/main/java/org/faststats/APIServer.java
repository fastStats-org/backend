package org.faststats;

import io.javalin.Javalin;
import org.faststats.route.project.CreateRoute;
import org.faststats.route.project.DeleteRoute;
import org.faststats.route.project.ListRoute;
import org.faststats.route.project.NoticeRoute;
import org.faststats.route.project.ProjectRoute;
import org.faststats.route.project.RenameRoute;
import org.faststats.route.project.SettingsRoute;

public class APIServer {
    private final Javalin javalin = Javalin.create(config -> {
        config.showJavalinBanner = false;
        config.useVirtualThreads = true;
    });

    public void registerRoutes() {
        CreateRoute.register(javalin);
        DeleteRoute.register(javalin);
        ListRoute.register(javalin);
        NoticeRoute.register(javalin);
        ProjectRoute.register(javalin);
        RenameRoute.register(javalin);
        SettingsRoute.register(javalin);
    }

    public void start() {
        var env = System.getenv("API_PORT");
        var port = env != null ? Integer.decode(env) : FastStats.CONFIG.apiPort();
        javalin.start(port);
    }
}
