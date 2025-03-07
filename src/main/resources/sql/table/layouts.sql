CREATE TABLE IF NOT EXISTS layouts
(
    "id"    INTEGER NOT NULL,
    "chart" TEXT    NOT NULL,
    "name"  TEXT    NOT NULL,
    "type"  TEXT    NOT NULL,
    "color" TEXT    NOT NULL,
    "index" INTEGER NOT NULL,
    "icon"  TEXT,
    "size"  INTEGER,
    UNIQUE ("id", "chart"),
    UNIQUE (id, "index"),
    FOREIGN KEY (id) REFERENCES projects (id) ON DELETE CASCADE
)