package org.faststats.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public record Layout(Map<String, Options> charts) {
    public JsonObject toJson() {
        var layout = new JsonObject();
        charts.forEach((id, options) -> layout.add(id, options.toJson()));
        return layout;
    }

    public record Options(String name, String type, String color, @Nullable String icon, @Nullable Integer size) {
        public JsonObject toJson() {
            var options = new JsonObject();
            if (icon != null) options.addProperty("icon", icon);
            if (size != null) options.addProperty("size", size);
            options.addProperty("color", color);
            options.addProperty("name", name);
            options.addProperty("type", type);
            return options;
        }

        public static Options fromJson(JsonObject options) {
            Preconditions.checkArgument(options.has("name"), "Name is required");
            Preconditions.checkArgument(options.has("type"), "Type is required");
            Preconditions.checkArgument(options.has("color"), "Color is required");
            var name = options.get("name").getAsString();
            var type = options.get("type").getAsString();
            var color = options.get("color").getAsString();
            var icon = options.has("icon") ? options.get("icon").getAsString() : null;
            var size = options.has("size") ? options.get("size").getAsInt() : null;
            return new Options(name, type, color, icon, size);
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
