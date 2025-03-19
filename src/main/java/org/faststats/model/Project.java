package org.faststats.model;

import com.google.gson.JsonObject;
import org.faststats.model.chart.ChartData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;

@NullMarked
public record Project(
        String name,
        String ownerId,
        String slug,
        int id,
        boolean isPrivate,
        @Nullable Layout layout,
        @Nullable String icon,
        @Nullable String previewChart,
        @Nullable String projectUrl,
        @Nullable ChartData data
) {
    public JsonObject toJson() {
        var object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("ownerId", ownerId);
        object.addProperty("private", isPrivate);
        object.addProperty("slug", slug);
        if (layout != null) object.add("layout", layout.toJson());
        if (icon != null) object.addProperty("icon", icon);
        if (previewChart != null) object.addProperty("previewChart", previewChart);
        if (projectUrl != null) object.addProperty("url", projectUrl);
        if (data != null) object.add("data", data.toJson());
        return object;
    }

    public Project withLayout(@Nullable Layout layout) {
        if (Objects.equals(layout, this.layout)) return this;
        return new Project(name, ownerId, slug, id, isPrivate, layout, icon, previewChart, projectUrl, null);
    }

    public Project withData(@Nullable ChartData data) {
        // if (Objects.equals(charts, this.charts)) return this;
        if (data == null) data = new ChartData(new LinkedHashSet<>(), 1000, 2000);

        //data.charts().add(new Chart("online_mode", 100, "Offline", null));
        //data.charts().add(new Chart("online_mode", 200, "Online", null));

        //data.charts().add(new Chart("os", 12, "MacOS", null));
        //data.charts().add(new Chart("os", 25, "Windows Server", null));
        //data.charts().add(new Chart("os", 2987345, "Linux", null));

        //data.charts().add(new Chart("players", 5, null, 1L));
        //data.charts().add(new Chart("players", 6, null, 2L));
        //data.charts().add(new Chart("players", 10, null, 3L));

        //data.charts().add(new Chart("servers", 12, null, 1L));
        //data.charts().add(new Chart("servers", 14, null, 2L));
        //data.charts().add(new Chart("servers", 16, null, 3L));

        return new Project(name, ownerId, slug, id, isPrivate, layout, icon, previewChart, projectUrl, data);
    }

    public static boolean isValidSlug(String slug) {
        return slug.matches("^(?=.{3,32}$)[a-z0-9]+(-[a-z0-9]+)*$");
    }

    public static boolean isValidChartId(String id) {
        return id.matches("^(?!.*__)[a-z0-9][a-z0-9_]{2,30}[a-z0-9]$");
    }
}
