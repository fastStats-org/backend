package org.faststats.route.project;

import com.google.gson.JsonArray;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class ListRoute {
    private final FastStats fastStats;

    public ListRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().get("/projects/{offset}/{limit}", this::projects);
        fastStats.javalin().get("/projects/{userId}", this::userProjects);
    }

    private void projects(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var limit = Integer.parseInt(context.pathParam("limit"));
                var offset = Integer.parseInt(context.pathParam("offset"));
                var projects = new JsonArray();
                fastStats.database().getProjects(offset, limit)
                        .forEach(projects::add);
                context.header("Content-Type", "application/json");
                context.result(projects.toString());
                context.status(200);
            } catch (NumberFormatException e) {
                context.status(400);
            }
        }));
    }

    private void userProjects(Context context) {
        context.future(() -> {
            context.status(200);
            return null;
        });
    }
}
