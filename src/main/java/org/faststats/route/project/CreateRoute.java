package org.faststats.route.project;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class CreateRoute {
    private final FastStats fastStats;

    public CreateRoute(FastStats fastStats) {
        this.fastStats = fastStats;
    }

    public void register() {
        fastStats.javalin().post("/project/new/{userId}/{projectName}", this::create);
    }

    private void create(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var userId = context.pathParam("userId");
                var projectName = context.pathParam("projectName");
                var body = JsonParser.parseString(context.body()).getAsJsonObject();
                var isPrivate = body.has("private") && body.get("private").getAsBoolean();
                var project = fastStats.database().createProject(userId, projectName, isPrivate);

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
