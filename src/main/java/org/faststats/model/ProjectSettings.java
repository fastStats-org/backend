package org.faststats.model;

import org.jspecify.annotations.Nullable;

public record ProjectSettings(
        @Nullable Boolean isPrivate
) {
}
