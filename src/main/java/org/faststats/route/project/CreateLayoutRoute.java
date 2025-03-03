package org.faststats.route.project;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Layout;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class CreateLayoutRoute {
    public static void register(Javalin javalin) {
        javalin.post("/project/layout/new/{projectId}", async(CreateLayoutRoute::handle));
    }

    private static void handle(Context context) {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));

            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            Preconditions.checkState(body.has("chart"), "Chart is required");
            var chart = body.get("chart").getAsString();
            var options = Layout.Options.fromJson(body);

            var success = FastStats.DATABASE.createChart(projectId, chart, options, ownerId);
            context.status(success ? 204 : 304);
        } catch (NumberFormatException | JsonSyntaxException | IllegalStateException e) {
            context.result(e.getMessage());
            context.status(400);
        } catch (SQLException e) {
            context.result(e.getMessage());
            context.status(409);
        }
    }
}
