package org.faststats;

import core.file.format.GsonFile;
import core.io.IO;
import io.javalin.Javalin;
import org.faststats.controller.DatabaseController;
import org.faststats.model.Config;
import org.faststats.route.MetricsRoute;
import org.faststats.route.project.CreateRoute;
import org.faststats.route.project.DeleteRoute;
import org.faststats.route.project.ListRoute;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FastStats {
    private final Config config = new GsonFile<>(IO.of("data", "config.json"), new Config(
            3000, "mongodb://localhost:27017", "*"
    )).validate().save().getRoot();

    private final DatabaseController databaseController = new DatabaseController(this);
    private final Javalin javalin = Javalin.create(config -> config.showJavalinBanner = false);

    public static void main(String[] args) {
        new FastStats().start();
    }

    public FastStats() {
        new CreateRoute(this).register();
        new DeleteRoute(this).register();
        new MetricsRoute(this).register();
        new ListRoute(this).register();
    }

    private void start() {
        javalin.start(Integer.getInteger("port", config.port()));
    }

    public Config config() {
        return config;
    }

    public DatabaseController database() {
        return databaseController;
    }

    public Javalin javalin() {
        return javalin;
    }
}
