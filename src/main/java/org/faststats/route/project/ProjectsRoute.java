package org.faststats.route.project;

import io.javalin.http.Context;
import org.faststats.FastStats;

public class ProjectsRoute {
    private final FastStats fastStats;

    public ProjectsRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().get("/projects", this::projects);
        fastStats.javalin().get("/projects/{userId}", this::userProjects);
    }

    private void projects(Context context) {
        context.future(() -> {
            context.status(200);
            return null;
        });
    }

    private void userProjects(Context context) {
        context.future(() -> {
            context.status(200);
            return null;
        });
    }
}
