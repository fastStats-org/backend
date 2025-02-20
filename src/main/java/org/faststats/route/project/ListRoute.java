package org.faststats.route.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class ListRoute {
    public static void register(Javalin javalin) {
        javalin.get("/project/list/{offset}/{limit}", ListRoute::projects);
    }

    private static void projects(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var param = context.queryParam("publicOnly");
                var publicOnly = param != null ? Boolean.parseBoolean(param) : null;
                var ownerId = context.queryParam("ownerId");

                var limit = Integer.parseInt(context.pathParam("limit"));
                var offset = Integer.parseInt(context.pathParam("offset"));

                var projects = new JsonArray();
                FastStats.DATABASE.getProjects(offset, limit, ownerId, publicOnly).forEach(projects::add);
                context.header("Content-Type", "application/json");
                context.result(projects.toString());
            } catch (IllegalStateException | JsonSyntaxException | NumberFormatException e) {
                context.status(400);
            }
        }));
    }
}
