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
import static org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;

@NullMarked
public class SetSlugRoute {
    public static void register(Javalin javalin) {
        javalin.head("/project/settings/slug/{slug}", async(SetSlugRoute::checkSlug));
        javalin.put("/project/settings/slug/{projectId}", async(SetSlugRoute::setSlug));
    }

    private static void checkSlug(Context context) throws SQLException {
        var slug = context.pathParam("slug");
        context.status(FastStats.DATABASE.isSlugUsed(slug) ? 409 : 204);
    }

    private static void setSlug(Context context) throws SQLException {
        try {
            var ownerId = context.queryParam("ownerId");
            var projectId = Integer.parseInt(context.pathParam("projectId"));
            var body = JsonParser.parseString(context.body()).getAsJsonObject();
            var slug = FastStats.nonnull(body, "slug", JsonElement::getAsString);
            var updated = FastStats.DATABASE.setProjectSlug(projectId, slug, ownerId);
            context.status(updated ? 204 : 304);
        } catch (IllegalArgumentException | JsonSyntaxException | IllegalStateException e) {
            error(context, e, 400);
        } catch (SQLiteException e) {
            if (e.getResultCode() != SQLITE_CONSTRAINT_UNIQUE) throw e;
            context.status(409);
        }
    }
}
