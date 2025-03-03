package org.faststats.route.projects;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

public class CountRoute {
    public static void register(Javalin javalin) {
        javalin.get("/projects/count/", async(CountRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var object = new JsonObject();
            object.addProperty("projects", FastStats.DATABASE.countProjects(ownerId));
            context.header("Content-Type", "application/json");
            context.result(object.toString());
            context.status(200);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
