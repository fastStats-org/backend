CREATE TABLE IF NOT EXISTS projects
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    owner         TEXT    NOT NULL,
    name          TEXT    NOT NULL,
    slug          TEXT    NOT NULL UNIQUE,
    private       BOOLEAN NOT NULL DEFAULT 0,
    layout_id     INTEGER,
    icon          TEXT,
    preview_chart INTEGER,
    url           TEXT,
    FOREIGN KEY (layout_id) REFERENCES layouts (id) ON DELETE CASCADE,
    UNIQUE (owner, name)
)