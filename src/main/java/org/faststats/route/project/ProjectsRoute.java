package org.faststats.route.project;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class ProjectsRoute {
    public static void register(Javalin javalin) {
        javalin.get("/projects/count/", ProjectsRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
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
        }));
    }
}
