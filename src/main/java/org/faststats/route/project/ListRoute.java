package org.faststats.route.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
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
    }

    private void projects(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var param = context.queryParam("publicOnly");
                var publicOnly = param != null ? Boolean.parseBoolean(param) : null;
                var userId = context.queryParam("userId");

                var limit = Integer.parseInt(context.pathParam("limit"));
                var offset = Integer.parseInt(context.pathParam("offset"));

                var projects = new JsonArray();
                fastStats.database().getProjects(offset, limit, userId, publicOnly).forEach(projects::add);
                context.header("Content-Type", "application/json");
                context.result(projects.toString());
            } catch (IllegalStateException | JsonSyntaxException | NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
