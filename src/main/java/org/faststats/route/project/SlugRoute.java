package org.faststats.route.project;

import io.javalin.Javalin;
import org.faststats.FastStats;

public class SlugRoute {
    public static void register(Javalin javalin) {
        javalin.head("/project/slug/{slug}", context -> {
            var slug = context.pathParam("slug");
            var slugUsed = FastStats.DATABASE.isSlugUsed(slug);
            context.status(slugUsed ? 409 : 204);
        });
    }
}
