package org.faststats.route.project;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Layout;
import org.jspecify.annotations.NullMarked;
import org.sqlite.SQLiteException;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;
import static org.faststats.route.RouteHandler.error;
import static org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;

@NullMarked
public class CreateLayoutRoute {
    public static void register(Javalin javalin) {
        javalin.post("/project/layout/new/{projectId}", async(CreateLayoutRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));

            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var options = Layout.Options.fromJson(body);

            var success = FastStats.DATABASE.createChart(projectId, options, ownerId);
            context.status(success ? 204 : 304);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException e) {
            error(context, e, 400);
        } catch (SQLiteException e) {
            if (e.getResultCode() != SQLITE_CONSTRAINT_UNIQUE) throw e;
            context.status(409);
        }
    }
}
