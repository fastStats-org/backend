package org.faststats.route.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var publicOnly = body.has("publicOnly") ? body.get("publicOnly").getAsBoolean() : null;
            var userId = body.has("userId") ? body.get("userId").getAsString() : null;
            userProjects(context, userId, publicOnly);
        }));
    }

    private void userProjects(Context context, @Nullable String userId, @Nullable Boolean publicOnly) {
        try {
            var limit = Integer.parseInt(context.pathParam("limit"));
            var offset = Integer.parseInt(context.pathParam("offset"));
            var projects = new JsonArray();
            fastStats.database().getProjects(offset, limit, userId, publicOnly).forEach(projects::add);
            context.header("Content-Type", "application/json");
            context.result(projects.toString());
        } catch (NumberFormatException e) {
            context.status(400);
        }
    }
}
