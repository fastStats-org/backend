package org.faststats.route.project;

import io.javalin.Javalin;

public class NoticeRoute {
    public static void register(Javalin javalin) {
        javalin.get("/", context -> {
            context.result("""
                This route should not be exposed to the outside!
                Check your firewall configuration.""");
            context.status(403);
        });
    }
}
