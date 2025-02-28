package org.faststats.route.project;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class RenameRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/rename/{projectId}/{name}", async(RenameRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var name = context.pathParam("name");
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var renamed = FastStats.DATABASE.renameProject(projectId, name, ownerId);
            context.status(renamed ? 204 : 409);
        } catch (NumberFormatException e) {
            context.result(e.getMessage());
            context.status(400);
        } catch (SQLException e) {
            context.result(e.getMessage());
            context.status(409);
        }
    }
}
