package org.faststats.route.project.settings;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class SetIconRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/icon/{projectId}", async(SetIconRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var icon = body.has("icon") ? body.get("icon").getAsString() : null;
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var updated = FastStats.DATABASE.updateIcon(projectId, icon, ownerId);
            context.status(updated ? 204 : 304);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException | SQLException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
