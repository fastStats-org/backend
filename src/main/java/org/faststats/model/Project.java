package org.faststats.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.faststats.model.chart.Chart;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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
        @Nullable Set<Chart> charts
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
        if (charts != null) {
            var array = new JsonArray();
            charts.forEach(chart -> array.add(chart.toJson()));
            object.add("charts", array);
        }
        return object;
    }

    public Project withLayout(@Nullable Layout layout) {
        if (Objects.equals(layout, this.layout)) return this;
        return new Project(name, ownerId, slug, id, isPrivate, layout, icon, previewChart, projectUrl, null);
    }

    public Project withCharts(@Nullable Set<Chart> charts) {
        // if (Objects.equals(charts, this.charts)) return this;
        if (charts == null) charts = new LinkedHashSet<>();

        charts.add(new Chart("online_mode", 100, "Offline", null));
        charts.add(new Chart("online_mode", 200, "Online", null));

        charts.add(new Chart("os", 12, "MacOS", null));
        charts.add(new Chart("os", 25, "Windows Server", null));
        charts.add(new Chart("os", 2987345, "Linux", null));

        charts.add(new Chart("players", 5, null, 1L));
        charts.add(new Chart("players", 6, null, 2L));
        charts.add(new Chart("players", 10, null, 3L));

        charts.add(new Chart("servers", 12, null, 1L));
        charts.add(new Chart("servers", 14, null, 2L));
        charts.add(new Chart("servers", 16, null, 3L));

        return new Project(name, ownerId, slug, id, isPrivate, layout, icon, previewChart, projectUrl, charts);
    }

    public static boolean isValidSlug(String slug) {
        return slug.matches("^(?=.{3,32}$)[a-z0-9]+(-[a-z0-9]+)*$");
    }

    public static boolean isValidChartId(String id) {
        return id.matches("^(?!.*__)[a-z0-9][a-z0-9_]{2,30}[a-z0-9]$");
    }
}
