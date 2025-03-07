package org.faststats.route.projects;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class ListRoute {
    public static void register(Javalin javalin) {
        javalin.get("/projects/list/{offset}/{limit}", async(ListRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var param = context.queryParam("publicOnly");
            var publicOnly = param != null ? Boolean.parseBoolean(param) : null;
            var ownerId = context.queryParam("ownerId");

            var limit = Integer.parseInt(context.pathParam("limit"));
            var offset = Integer.parseInt(context.pathParam("offset"));

            var projects = new JsonArray();
            FastStats.DATABASE.getProjects(offset, limit, ownerId, publicOnly)
                    .stream().map(Project::toJson).forEach(projects::add);
            context.header("Content-Type", "application/json");
            context.result(projects.toString());
            context.status(200);
        } catch (IllegalStateException | JsonSyntaxException | NumberFormatException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }
}
