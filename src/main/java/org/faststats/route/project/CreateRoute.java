package org.faststats.route.project;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@NullMarked
public class CreateRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateRoute.class);

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
                var name = body.get("name").getAsString();
                var project = FastStats.DATABASE.createProject(name, ownerId, isPrivate);
                context.header("Content-Type", "application/json");
                context.result(project.toJson().toString());
                context.status(200);
            } catch (IllegalStateException | JsonSyntaxException e) {
                context.result(e.getMessage());
                context.status(400);
            } catch (SQLException e) {
                context.status(409);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.status(500).result(throwable.getMessage());
            return null;
        }));
    }
}
