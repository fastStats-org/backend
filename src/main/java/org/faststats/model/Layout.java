package org.faststats.model;

import com.google.gson.JsonObject;
import org.bson.Document;
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

    public Document toDocument() {
        var document = new Document();
        charts.forEach((key, value) -> document.put(key, value.toDocument()));
        return document;
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

        public Document toDocument() {
            var document = new Document();
            if (icon != null) document.put("icon", icon);
            if (size != null) document.put("size", size);
            document.put("color", color);
            document.put("name", name);
            document.put("type", type);
            return document;
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
