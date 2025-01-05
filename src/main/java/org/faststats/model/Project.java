package org.faststats.model;

import com.google.gson.Gson;

public record Project(
        String name,
        String userId,
        int id
) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
