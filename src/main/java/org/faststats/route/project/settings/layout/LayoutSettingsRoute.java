package org.faststats.route.project.settings.layout;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.faststats.FastStats;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.function.Function;

import static org.faststats.route.RouteHandler.async;

public class LayoutSettingsRoute {
    public static void register(@NonNull Javalin javalin) {
        javalin.put("/project/settings/layout/color/{projectId}/{chart}", async(LayoutSettingsRoute::setColor));
        javalin.put("/project/settings/layout/icon/{projectId}/{chart}", async(LayoutSettingsRoute::setIcon));
        javalin.put("/project/settings/layout/id/{projectId}/{chart}", async(LayoutSettingsRoute::setId));
        javalin.put("/project/settings/layout/name/{projectId}/{chart}", async(LayoutSettingsRoute::setName));
        javalin.put("/project/settings/layout/size/{projectId}/{chart}", async(LayoutSettingsRoute::setSize));
        javalin.put("/project/settings/layout/type/{projectId}/{chart}", async(LayoutSettingsRoute::setType));
    }

    private static void setColor(@NonNull Context context) throws SQLException {
        setComponent(context, "color", JsonElement::getAsString, FastStats.DATABASE::setChartColor);
    }

    private static void setIcon(@NonNull Context context) throws SQLException {
        setComponent(context, "icon", JsonElement::getAsString, FastStats.DATABASE::setChartIcon);
    }

    private static void setId(@NonNull Context context) throws SQLException {
        setComponent(context, "id", JsonElement::getAsString, FastStats.DATABASE::setChartId);
    }

    private static void setName(@NonNull Context context) throws SQLException {
        setComponent(context, "name", JsonElement::getAsString, FastStats.DATABASE::setChartName);
    }

    private static void setSize(@NonNull Context context) throws SQLException {
        setComponent(context, "size", JsonElement::getAsInt, FastStats.DATABASE::setChartSize);
    }

    private static void setType(@NonNull Context context) throws SQLException {
        setComponent(context, "type", JsonElement::getAsString, FastStats.DATABASE::setChartType);
    }

    private static <T> void setComponent(@NonNull Context context, @NonNull String component, @NonNull Function<JsonElement, T> transformer, @NonNull Setter<T> setter) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var chart = context.pathParam("chart");
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var value = FastStats.nullable(body, component, transformer);
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            context.status(setter.set(projectId, chart, value, ownerId) ? 204 : 304);
        } catch (IllegalArgumentException | IllegalStateException e) {
            context.result(e.getMessage());
            context.status(400);
        }
    }

    @FunctionalInterface
    private interface Setter<T> {
        boolean set(int projectId, @NonNull String chart, T parameter, @Nullable String ownerId) throws SQLException;
    }
}
