package org.faststats.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.faststats.model.chart.Chart;
import org.faststats.route.metric.ChartDataGen;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
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
        charts = new HashSet<>();
        charts.addAll(ChartDataGen.generateRandomChartData(5, "random"));
        charts.addAll(ChartDataGen.generateTimeSeriesData(7, previewChart != null ? previewChart : "players"));
        charts.addAll(ChartDataGen.generateMultiSeriesData(2, 3));
        return new Project(name, ownerId, slug, id, isPrivate, layout, icon, previewChart, projectUrl, charts);
    }

    public static boolean isValidSlug(String slug) {
        return slug.matches("^(?=.{3,32}$)[a-z0-9]+(-[a-z0-9]+)*$");
    }

    public static boolean isValidChartId(String id) {
        return id.matches("^(?!.*__)[a-z0-9][a-z0-9_]{2,30}[a-z0-9]$");
    }
}
