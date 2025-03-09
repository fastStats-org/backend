package org.faststats.route.project;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NullMarked;
import org.sqlite.SQLiteException;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;
import static org.faststats.route.RouteHandler.error;
import static org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;

@NullMarked
public class CreateRoute {
    public static void register(Javalin javalin) {
        javalin.post("/project/new", async(CreateRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            Preconditions.checkState(body.has("name"), "Name is required");
            Preconditions.checkState(body.has("ownerId"), "Owner ID is required");
            var isPrivate = body.has("private") && body.get("private").getAsBoolean();
            var ownerId = body.get("ownerId").getAsString();
            var name = body.get("name").getAsString();
            var project = FastStats.DATABASE.createProject(name, ownerId, isPrivate);
            context.header("Content-Type", "application/json");
            context.result(project.toJson().toString());
            context.status(200);
        } catch (IllegalStateException | JsonSyntaxException e) {
            error(context, e, 400);
        } catch (SQLiteException e) {
            if (e.getResultCode() != SQLITE_CONSTRAINT_UNIQUE) throw e;
            context.status(409);
        }
    }
}
