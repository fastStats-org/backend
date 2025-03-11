package org.faststats.model.chart;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;

public record ChartData(
        Set<Chart> charts,
        long from,
        long to
) {

    public JsonObject toJson() {
        var object = new JsonObject();
        var charts = new JsonArray();
        this.charts.forEach(chart -> charts.add(chart.toJson()));
        object.add("charts", charts);
        object.addProperty("from", from);
        object.addProperty("to", to);
        return object;
========
public class ChartData {
    public static ChartData fromJson(Type type, JsonElement chart) {
        return new ChartData();
>>>>>>>> 8385886 (Refactor chart model and remove color property):src/main/java/org/faststats/model/chart/ChartData.java
    }

    public enum Type {
        AREA("area", "area_linear", "area_step", "area_stacked", "area_expanded", "area_legend", "area_icons", "area_gradient", "area_axes", "area_interactive"),
        BAR("bar", "bar_horizontal", "bar_multiple", "bar_label", "bar_custom_label", "bar_mixed", "bar_stacked_legend", "bar_active", "bar_negative", "bar_interactive"),
        LINE("line", "line_linear", "line_step", "line_multiple", "line_dots", "line_custom_dots", "line_dots_colors", "line_label", "line_custom_label", "line_interactive"),
        PIE("pie", "pie_separator_none", "pie_label", "pie_custom_label", "pie_label_list", "pie_legend", "pie_donut", "pie_donut_active", "pie_donut_text", "pie_stacked", "pie_interactive"),
        RADAR("radar", "radar_dots", "radar_multiple", "radar_lines_only", "radar_custom_label", "radar_radius_axis", "radar_grid_custom", "radar_grid_filled", "radar_grid_none", "radar_grid_circle", "radar_grid_circle_no_lines", "radar_grid_circle_filled", "radar_legend", "radar_icons"),
        RADIAL("radial", "radial_label", "radial_grid", "radial_text", "radial_shape", "radial_stacked"),
        //MAP("map")
        ;
        private final Set<String> names;

        Type(String... names) {
            this.names = Set.of(names);
        }

        public static @Nullable Type fromString(String type) {
            return Arrays.stream(values())
                    .filter(t -> t.names.contains(type))
                    .findFirst().orElse(null);
        }
    }
}
