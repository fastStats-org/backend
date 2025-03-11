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
public record Layout(Set<Chart> charts) {
    public JsonArray toJson() {
        var layout = new JsonArray();
        charts.forEach(chart -> layout.add(chart.toJson()));
        return layout;
    }

    public record Chart(
            String chart,
            String name,
            String type,
            boolean staticInfo,
            Dimensions dimensions,
            int position,
            @Nullable String icon,
            Set<Source> sources,
            JsonObject extras
    ) {
        public JsonObject toJson() {
            var options = new JsonObject();
            if (icon != null) options.addProperty("icon", icon);
            options.add("dimensions", dimensions.toJson());
            options.addProperty("chart", chart);
            options.addProperty("static", staticInfo);
            options.addProperty("position", position);
            options.addProperty("name", name);
            options.addProperty("type", type);
            var sources = new JsonArray();
            this.sources.stream().map(Source::toJson).forEach(sources::add);
            options.add("sources", sources);
            options.add("extras", extras);
            return options;
        }

        public static Chart fromJson(JsonObject options) {
            Preconditions.checkState(options.has("chart"), "Chart is required");
            Preconditions.checkState(options.has("name"), "Name is required");
            Preconditions.checkState(options.has("type"), "Type is required");
            Preconditions.checkState(options.has("static"), "Static is required");
            Preconditions.checkState(options.has("position"), "Position is required");
            Preconditions.checkState(options.has("dimensions"), "Dimensions are required");
            Preconditions.checkState(options.has("sources"), "Sources are required");
            var chart = options.get("chart").getAsString();
            var name = options.get("name").getAsString();
            var type = options.get("type").getAsString();
            var staticInfo = options.get("static").getAsBoolean();
            var position = options.get("position").getAsInt();
            var icon = options.has("icon") ? options.get("icon").getAsString() : null;
            var dimensions = Dimensions.fromJson(options.getAsJsonObject("dimensions"));
            var sources = Layout.Source.fromJson(options.getAsJsonArray("sources"));
            var extras = options.has("extras") ? options.getAsJsonObject("extras") : new JsonObject();
            return new Chart(chart, name, type, staticInfo, dimensions, position, icon, sources, extras);
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
        var charts = new HashSet<Chart>();
        layout.forEach(entry -> charts.add(Chart.fromJson(entry.getAsJsonObject())));
        return charts.isEmpty() ? null : new Layout(charts);
    }

    public static Map<String, Integer> readPositions(JsonObject object) {
        var positions = new HashMap<String, Integer>();
        object.entrySet().forEach(entry -> positions.put(entry.getKey(), entry.getValue().getAsInt()));
        return positions;
    }

    public record Source(
            String key,
            String name,
            String color,
            @Nullable String value
    ) {
        public JsonObject toJson() {
            var object = new JsonObject();
            object.addProperty("key", key);
            object.addProperty("name", name);
            object.addProperty("color", color);
            if (value != null) object.addProperty("value", value);
            return object;
        }

        public static Set<Source> fromJson(JsonArray array) {
            var sources = new HashSet<Source>();
            array.forEach(entry -> sources.add(fromJson(entry.getAsJsonObject())));
            return sources;
        }

        public static Source fromJson(JsonObject object) {
            Preconditions.checkState(object.has("key"), "Key is required");
            Preconditions.checkState(object.has("name"), "Name is required");
            Preconditions.checkState(object.has("color"), "Color is required");
            var key = object.get("key").getAsString();
            var name = object.get("name").getAsString();
            var color = object.get("color").getAsString();
            var value = object.has("value") ? object.get("value").getAsString() : null;
            return new Source(key, name, color, value);
        }

        public static JsonArray toJson(Set<Source> sources) {
            var array = new JsonArray();
            sources.forEach(source -> array.add(source.toJson()));
            return array;
        }
    }
}
