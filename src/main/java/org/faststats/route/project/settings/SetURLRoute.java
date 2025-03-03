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
public class SetURLRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/url/{projectId}", async(SetURLRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var url = body.has("url") ? body.get("url").getAsString() : null;
            var updated = FastStats.DATABASE.updateUrl(projectId, url, ownerId);
            context.status(updated ? 204 : 304);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException | SQLException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
