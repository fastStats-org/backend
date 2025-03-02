CREATE TABLE IF NOT EXISTS projects
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    owner         TEXT    NOT NULL,
    name          TEXT    NOT NULL,
    slug          TEXT    NOT NULL UNIQUE,
    private       BOOLEAN NOT NULL DEFAULT 0,
    icon          TEXT,
    preview_chart TEXT,
    url           TEXT,
    FOREIGN KEY (id, preview_chart) REFERENCES layouts (id, chart),
    UNIQUE (owner, name)
)