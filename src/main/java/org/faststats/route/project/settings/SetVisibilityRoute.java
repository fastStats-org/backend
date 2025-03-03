package org.faststats.route.project.settings;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class SetVisibilityRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/private/{projectId}", async(SetVisibilityRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            Preconditions.checkState(body.has("private"), "Visibility is required");
            var isPrivate = body.get("private").getAsBoolean();
            var updated = FastStats.DATABASE.updateVisibility(projectId, isPrivate, ownerId);
            context.status(updated ? 204 : 304);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException | SQLException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
