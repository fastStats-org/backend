package org.faststats.route.project;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class CreateRoute {
    public static void register(Javalin javalin) {
        javalin.post("/project/new/{userId}/{projectName}/{slug}", CreateRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var slug = context.pathParam("slug");
                var userId = context.pathParam("userId");
                var projectName = context.pathParam("projectName");
                var body = JsonParser.parseString(context.body()).getAsJsonObject();
                var isPrivate = body.has("private") && body.get("private").getAsBoolean();
                var project = FastStats.DATABASE.createProject(userId, projectName, slug, isPrivate);

                if (project != null) {
                    context.header("Content-Type", "application/json");
                    context.result(project.toString());
                    context.status(200);
                } else {
                    context.status(409);
                }
            } catch (IllegalStateException | JsonSyntaxException e) {
                context.status(400);
            }
        }));
    }
}
