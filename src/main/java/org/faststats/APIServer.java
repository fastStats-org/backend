package org.faststats;

import io.javalin.Javalin;
import org.faststats.route.NoticeRoute;
import org.faststats.route.project.CreateLayoutRoute;
import org.faststats.route.project.CreateRoute;
import org.faststats.route.project.DeleteRoute;
import org.faststats.route.project.ProjectRoute;
import org.faststats.route.project.settings.SetIconRoute;
import org.faststats.route.project.settings.SetNameRoute;
import org.faststats.route.project.settings.SetPreviewRoute;
import org.faststats.route.project.settings.SetSlugRoute;
import org.faststats.route.project.settings.SetURLRoute;
import org.faststats.route.project.settings.SetVisibilityRoute;
import org.faststats.route.project.settings.layout.LayoutSettingsRoute;
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

        CountRoute.register(javalin);
        ListRoute.register(javalin);

        LayoutSettingsRoute.register(javalin);
        SetIconRoute.register(javalin);
        SetNameRoute.register(javalin);
        SetPreviewRoute.register(javalin);
        SetSlugRoute.register(javalin);
        SetURLRoute.register(javalin);
        SetVisibilityRoute.register(javalin);

        NoticeRoute.register(javalin);
    }

    public void start() {
        javalin.start(FastStats.CONFIG.apiPort());
    }
}
