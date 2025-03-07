package org.faststats.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public record Layout(Map<String, Options> charts) {
    public JsonArray toJson() {
        var layout = new JsonArray();
        charts.forEach((chart, options) -> {
            var json = options.toJson();
            json.addProperty("chart", chart);
            layout.add(json);
        });
        return layout;
    }

    public record Options(
            String name,
            String type,
            String color,
            Dimensions dimensions,
            int index,
            @Nullable String icon
    ) {
        public JsonObject toJson() {
            var options = new JsonObject();
            if (icon != null) options.addProperty("icon", icon);
            options.add("dimensions", dimensions.toJson());
            options.addProperty("color", color);
            options.addProperty("index", index);
            options.addProperty("name", name);
            options.addProperty("type", type);
            return options;
        }

        public static Options fromJson(JsonObject options) {
            Preconditions.checkState(options.has("name"), "Name is required");
            Preconditions.checkState(options.has("type"), "Type is required");
            Preconditions.checkState(options.has("color"), "Color is required");
            Preconditions.checkState(options.has("index"), "Index is required");
            Preconditions.checkState(options.has("dimensions"), "Dimensions are required");
            var name = options.get("name").getAsString();
            var type = options.get("type").getAsString();
            var color = options.get("color").getAsString();
            var index = options.get("index").getAsInt();
            var icon = options.has("icon") ? options.get("icon").getAsString() : null;
            var dimensions = Dimensions.fromJson(options.getAsJsonObject("dimensions"));
            return new Options(name, type, color, dimensions, index, icon);
        }
    }

    public record Dimensions(int width, int height) {
        public JsonObject toJson() {
            var dimensions = new JsonObject();
            dimensions.addProperty("width", width);
            dimensions.addProperty("height", height);
            return dimensions;
        }

        public static Dimensions fromJson(JsonElement element) {
            Preconditions.checkState(element.isJsonObject(), "Expected json object");
            var dimensions = element.getAsJsonObject();
            Preconditions.checkState(dimensions.has("width"), "Width is required");
            Preconditions.checkState(dimensions.has("height"), "Height is required");
            var width = dimensions.get("width").getAsInt();
            var height = dimensions.get("height").getAsInt();
            return new Dimensions(width, height);
        }
    }

    public static @Nullable Layout fromJson(JsonObject layout) {
        var charts = new HashMap<String, Options>();
        layout.entrySet().forEach(entry -> {
            var options = Options.fromJson(entry.getValue().getAsJsonObject());
            charts.put(entry.getKey(), options);
        });
        return charts.isEmpty() ? null : new Layout(charts);
    }
}
