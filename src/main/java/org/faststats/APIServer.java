package org.faststats;

import io.javalin.Javalin;
import org.faststats.route.NoticeRoute;
import org.faststats.route.project.CreateRoute;
import org.faststats.route.project.DeleteRoute;
import org.faststats.route.project.ProjectRoute;
import org.faststats.route.project.RenameRoute;
import org.faststats.route.project.SlugRoute;
import org.faststats.route.project.layout.CreateLayoutRoute;
import org.faststats.route.project.settings.IconSettingsRoute;
import org.faststats.route.project.settings.LayoutSettingsRoute;
import org.faststats.route.project.settings.PreviewSettingsRoute;
import org.faststats.route.project.settings.SlugSettingsRoute;
import org.faststats.route.project.settings.URLSettingsRoute;
import org.faststats.route.project.settings.VisibilitySettingsRoute;
import org.faststats.route.projects.CountRoute;
import org.faststats.route.projects.ListRoute;

public class APIServer {
    private final Javalin javalin = Javalin.create(config -> {
        config.showJavalinBanner = false;
        config.useVirtualThreads = false;
    });

    public void registerRoutes() {
        CreateLayoutRoute.register(javalin);

        CreateRoute.register(javalin);
        DeleteRoute.register(javalin);
        ProjectRoute.register(javalin);
        RenameRoute.register(javalin);
        SlugRoute.register(javalin);

        CountRoute.register(javalin);
        ListRoute.register(javalin);

        IconSettingsRoute.register(javalin);
        LayoutSettingsRoute.register(javalin);
        PreviewSettingsRoute.register(javalin);
        SlugSettingsRoute.register(javalin);
        URLSettingsRoute.register(javalin);
        VisibilitySettingsRoute.register(javalin);

        NoticeRoute.register(javalin);
    }

    public void start() {
        var env = System.getenv("API_PORT");
        var port = env != null ? Integer.decode(env) : FastStats.CONFIG.apiPort();
        javalin.start(port);
    }
}
