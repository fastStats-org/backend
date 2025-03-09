package org.faststats.model.chart;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    }
}
