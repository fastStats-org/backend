package org.faststats.route.project.settings;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Layout;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@NullMarked
public class LayoutSettingsRoute {
    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutSettingsRoute.class);

    public static void register(Javalin javalin) {
        javalin.put("/project/settings/layout/new/{projectId}", LayoutSettingsRoute::create);
        javalin.put("/project/settings/layout/rename/{projectId}/{chart}/{name}", LayoutSettingsRoute::rename);
    }

    private static void create(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
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
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.result(throwable.getMessage()).status(500);
            return null;
        }));
    }

    private static void rename(Context context) {
        context.future(() -> CompletableFuture.runAsync(() -> {
            try {
                var ownerId = context.queryParam("ownerId");
                var chart = context.pathParam("chart");
                var name = context.pathParam("name");
                var projectId = Integer.parseInt(context.pathParam("projectId"));
                var updated = FastStats.DATABASE.renameChart(projectId, chart, name, ownerId);
                context.status(updated ? 204 : 304);
            } catch (NumberFormatException | SQLException e) {
                context.result(e.getMessage());
                context.status(400);
            }
        }).orTimeout(5, TimeUnit.SECONDS).exceptionally(throwable -> {
            LOGGER.error("Failed to handle request", throwable);
            context.result(throwable.getMessage()).status(500);
            return null;
        }));
    }
}
