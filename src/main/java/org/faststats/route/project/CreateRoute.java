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
        javalin.post("/project/new", CreateRoute::handle);
    }

    private static void handle(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var body = JsonParser.parseString(context.body()).getAsJsonObject();
                if (!body.has("name") || !body.has("ownerId")) throw new IllegalStateException();

                var isPrivate = body.has("private") && body.get("private").getAsBoolean();
                var ownerId = body.get("ownerId").getAsString();
                var projectName = body.get("name").getAsString();
                var project = FastStats.DATABASE.createProject(ownerId, projectName, isPrivate);

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
