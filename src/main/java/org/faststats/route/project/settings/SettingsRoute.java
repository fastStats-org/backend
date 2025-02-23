package org.faststats.route.project.settings;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.ProjectSettings;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@NullMarked
@Deprecated(forRemoval = true)
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

                if (settings == null || settings.isEmpty()) {
                    context.status(400);
                    return;
                }
                if (!settings.isValid()) {
                    context.status(400);
                    return;
                }

                var updated = FastStats.DATABASE.updateProject(projectId, settings, ownerId);
                context.status(updated ? 204 : 304);
            } catch (IllegalStateException | JsonSyntaxException | NumberFormatException | SQLException e) {
                context.status(400);
            }
        }));
    }
}
