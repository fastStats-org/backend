package org.faststats.route.project.settings;

import com.google.gson.JsonElement;
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
import static org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_FOREIGNKEY;

@NullMarked
public class SetPreviewRoute {
    public static void register(Javalin javalin) {
        javalin.put("/project/settings/preview/{projectId}", async(SetPreviewRoute::handle));
    }

    private static void handle(Context context) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var chart = FastStats.nullable(body, "chart", JsonElement::getAsString);
            var updated = FastStats.DATABASE.setProjectPreviewChart(projectId, chart, ownerId);
            context.status(updated ? 204 : 304);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException e) {
            error(context, e, 400);
        } catch (SQLiteException e) {
            if (e.getResultCode() != SQLITE_CONSTRAINT_FOREIGNKEY) throw e;
            context.status(404);
        }
    }
}
