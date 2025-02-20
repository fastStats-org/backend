package org.faststats.route.project;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.ProjectSettings;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class SettingsRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/{projectId}", SettingsRoute::settings);
    }

    private static void settings(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var body = JsonParser.parseString(context.body());
                var settings = body.isJsonObject() ? ProjectSettings.fromJson(body.getAsJsonObject()) : null;
                context.status(FastStats.DATABASE.updateProject(projectId, settings, ownerId));
            } catch (IllegalStateException | JsonSyntaxException | NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
