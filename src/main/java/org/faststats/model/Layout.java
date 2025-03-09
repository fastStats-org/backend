package org.faststats.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NullMarked
public record Layout(Set<Options> charts) {
    public JsonArray toJson() {
        var layout = new JsonArray();
        charts.forEach(options -> layout.add(options.toJson()));
        return layout;
    }

    public record Options(
            String chart,
            String name,
            String type,
            String color,
            Dimensions dimensions,
            int position,
            @Nullable String icon
    ) {
        public JsonObject toJson() {
            var options = new JsonObject();
            if (icon != null) options.addProperty("icon", icon);
            options.add("dimensions", dimensions.toJson());
            options.addProperty("chart", chart);
            options.addProperty("color", color);
            options.addProperty("position", position);
            options.addProperty("name", name);
            options.addProperty("type", type);
            return options;
        }

        public static Options fromJson(JsonObject options) {
            Preconditions.checkState(options.has("chart"), "Chart is required");
            Preconditions.checkState(options.has("name"), "Name is required");
            Preconditions.checkState(options.has("type"), "Type is required");
            Preconditions.checkState(options.has("color"), "Color is required");
            Preconditions.checkState(options.has("position"), "Position is required");
            Preconditions.checkState(options.has("dimensions"), "Dimensions are required");
            var chart = options.get("chart").getAsString();
            var name = options.get("name").getAsString();
            var type = options.get("type").getAsString();
            var color = options.get("color").getAsString();
            var position = options.get("position").getAsInt();
            var icon = options.has("icon") ? options.get("icon").getAsString() : null;
            var dimensions = Dimensions.fromJson(options.getAsJsonObject("dimensions"));
            return new Options(chart, name, type, color, dimensions, position, icon);
        }
    }

    public record Dimensions(int width, int height) {
        public JsonObject toJson() {
            var dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            return dimensions;
        }

        public static Dimensions fromJson(JsonObject dimensions) {
            Preconditions.checkState(dimensions.has("width"), "Width is required");
            Preconditions.checkState(dimensions.has("height"), "Height is required");
            var width = dimensions.get("width").getAsInt();
            var height = dimensions.get("height").getAsInt();
            return new Dimensions(width, height);
        }
    }

    public static @Nullable Layout fromJson(JsonArray layout) {
        var charts = new HashSet<Options>();
        layout.forEach(entry -> charts.add(Options.fromJson(entry.getAsJsonObject())));
        return charts.isEmpty() ? null : new Layout(charts);
    }

    public static Map<String, Integer> readPositions(JsonObject object) {
        var positions = new HashMap<String, Integer>();
        object.entrySet().forEach(entry -> positions.put(entry.getKey(), entry.getValue().getAsInt()));
        return positions;
    }
}
