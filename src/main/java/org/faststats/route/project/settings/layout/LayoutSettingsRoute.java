package org.faststats.route.project.settings.layout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.faststats.model.Layout;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.function.Function;

import static org.faststats.FastStats.nullable;
import static org.faststats.route.RouteHandler.async;
import static org.faststats.route.RouteHandler.error;

public class LayoutSettingsRoute {
    public static void register(@NonNull Javalin javalin) {
        javalin.put("/project/settings/layout/dimensions/{projectId}/{chart}", async(LayoutSettingsRoute::setDimensions));
        javalin.put("/project/settings/layout/icon/{projectId}/{chart}", async(LayoutSettingsRoute::setIcon));
        javalin.put("/project/settings/layout/id/{projectId}/{chart}", async(LayoutSettingsRoute::setId));
        javalin.put("/project/settings/layout/name/{projectId}/{chart}", async(LayoutSettingsRoute::setName));
        javalin.put("/project/settings/layout/positions/{projectId}", async(LayoutSettingsRoute::setPosition));
        javalin.put("/project/settings/layout/type/{projectId}/{chart}", async(LayoutSettingsRoute::setType));
    }

    private static void setDimensions(@NonNull Context context) throws SQLException {
        setComponent(context, Layout.Dimensions::fromJson, FastStats.DATABASE::setChartDimensions);
    }

    private static void setIcon(@NonNull Context context) throws SQLException {
        setComponent(context, o -> nullable(o, "icon", JsonElement::getAsString), FastStats.DATABASE::setChartIcon);
    }

    private static void setId(@NonNull Context context) throws SQLException {
        setComponent(context, o -> nullable(o, "id", JsonElement::getAsString), FastStats.DATABASE::setChartId);
    }

    private static void setName(@NonNull Context context) throws SQLException {
        setComponent(context, o -> nullable(o, "name", JsonElement::getAsString), FastStats.DATABASE::setChartName);
    }

    private static void setPosition(@NonNull Context context) throws SQLException {
        try {
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var ownerId = context.queryParam("ownerId");
            var positions = Layout.readPositions(JsonParser.parseString(context.body()).getAsJsonObject());
            context.status(FastStats.DATABASE.setChartPositions(projectId, positions, ownerId) ? 204 : 304);
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(context, e, 400);
        }
    }

    private static void setType(@NonNull Context context) throws SQLException {
        setComponent(context, o -> nullable(o, "type", JsonElement::getAsString), FastStats.DATABASE::setChartType);
    }

    private static <T> void setComponent(@NonNull Context context, @NonNull Function<JsonObject, T> transformer, @NonNull Setter<T> setter) throws SQLException {
        try {
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var ownerId = context.queryParam("ownerId");
            var chart = context.pathParam("chart");
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var value = transformer.apply(body);
            context.status(setter.set(projectId, chart, value, ownerId) ? 204 : 304);
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(context, e, 400);
        }
    }

    @FunctionalInterface
    private interface Setter<T> {
        boolean set(int projectId, @NonNull String chart, T parameter, @Nullable String ownerId) throws SQLException;
    }
}
