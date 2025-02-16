package org.faststats.model;

import com.google.gson.JsonObject;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public record Layout(Map<String, Options> charts) {
    public JsonObject toJson() {
        var object = new JsonObject();
        charts.forEach((key, value) -> object.add(key, value.toJson()));
        return object;
    }

    public BSONObject toBson() {
        var object = new BasicBSONObject();
        charts.forEach((key, value) -> object.put(key, value.toBson()));
        return object;
    }

    public record Options(String name, String type, String color, @Nullable String icon, @Nullable Integer size) {
        public JsonObject toJson() {
            var object = new JsonObject();
            if (icon != null) object.addProperty("icon", icon);
            if (size != null) object.addProperty("size", size);
            object.addProperty("color", color);
            object.addProperty("name", name);
            object.addProperty("type", type);
            return object;
        }

        public BSONObject toBson() {
            var object = new BasicBSONObject();
            if (icon != null) object.put("icon", icon);
            if (size != null) object.put("size", size);
            object.put("color", color);
            object.put("name", name);
            object.put("type", type);
            return object;
        }

        public static Options fromJson(JsonObject options) {
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
