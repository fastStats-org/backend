package org.faststats.route.project;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.ProjectSettings;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class SettingsRoute {
    private final FastStats fastStats;

    public SettingsRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().put("/project/{projectId}", this::settings);
    }

    private void settings(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var body = JsonParser.parseString(context.body()).getAsJsonObject();

                var settings = new ProjectSettings(
                        body.has("private") ? body.get("private").getAsBoolean() : null
                );

                var updated = fastStats.database().updateProject(projectId, settings);
                context.status(updated == null ? 404 : updated ? 200 : 409);
            } catch (JsonSyntaxException | NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
