package org.faststats.route.project.settings;

import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Layout;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;

import static org.faststats.route.RouteHandler.async;

@NullMarked
public class LayoutSettingsRoute {
    public static void register(Javalin javalin) {
        javalin.post("/project/layout/new/{projectId}", async(LayoutSettingsRoute::create));
        javalin.put("/project/settings/layout/color/{projectId}/{chart}/{color}", async(LayoutSettingsRoute::setColor));
        javalin.put("/project/settings/layout/icon/{projectId}/{chart}/{icon}", async(LayoutSettingsRoute::setIcon));
        javalin.put("/project/settings/layout/id/{projectId}/{chart}/{id}", async(LayoutSettingsRoute::setId));
        javalin.put("/project/settings/layout/name/{projectId}/{chart}/{name}", async(LayoutSettingsRoute::setName));
        javalin.put("/project/settings/layout/size/{projectId}/{chart}/{size}", async(LayoutSettingsRoute::setSize));
        javalin.put("/project/settings/layout/type/{projectId}/{chart}/{type}", async(LayoutSettingsRoute::setType));
    }

    private static void create(Context context) {
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

    private static void setComponent(Context context, String component, Setter setter) {
        try {
            var ownerId = context.queryParam("ownerId");
            var chart = context.pathParam("chart");
            var value = context.pathParam(component);
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            context.status(setter.set(projectId, chart, value, ownerId) ? 204 : 304);
        } catch (NumberFormatException | SQLException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }

    private static void setColor(Context context) {
        setComponent(context, "color", FastStats.DATABASE::setChartColor);
    }

    private static void setIcon(Context context) {
        setComponent(context, "icon", FastStats.DATABASE::setChartIcon);
    }

    private static void setId(Context context) {
        setComponent(context, "id", FastStats.DATABASE::setChartId);
    }

    private static void setName(Context context) {
        setComponent(context, "name", FastStats.DATABASE::setChartName);
    }

    private static void setSize(Context context) {
        setComponent(context, "size", (projectId, chart, parameter, ownerId) ->
                FastStats.DATABASE.setChartSize(projectId, chart, Integer.parseInt(parameter), ownerId));
    }

    private static void setType(Context context) {
        setComponent(context, "type", FastStats.DATABASE::setChartType);
    }

    @FunctionalInterface
    private interface Setter {
        boolean set(int projectId, String chart, String parameter, @Nullable String ownerId) throws SQLException;
    }
}
