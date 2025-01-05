package org.faststats.model;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Config(
        @SerializedName("port") int port
) {
}
