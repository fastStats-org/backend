CREATE TABLE IF NOT EXISTS layouts
(
    id    INTEGER NOT NULL,
    chart TEXT    NOT NULL,
    name  TEXT    NOT NULL,
    type  TEXT    NOT NULL,
    color TEXT    NOT NULL,
    icon  TEXT,
    size  INTEGER,
    UNIQUE (id, chart),
    "index" INTEGER NOT NULL,
    UNIQUE ("id", "chart"),
    FOREIGN KEY (id) REFERENCES projects (id) ON DELETE CASCADE
)