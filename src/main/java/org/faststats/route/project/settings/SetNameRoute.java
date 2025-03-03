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
public class SetNameRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/name/{projectId}", async(SetNameRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            Preconditions.checkState(body.has("name"), "Name is required");
            var name = body.get("name").getAsString();
            var renamed = FastStats.DATABASE.renameProject(projectId, name, ownerId);
            context.status(renamed ? 204 : 409);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException e) {
            context.result(e.getMessage());
            context.status(400);
        } catch (SQLException e) {
            context.result(e.getMessage());
            context.status(409);
        }
    }
}
