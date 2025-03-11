CREATE TABLE IF NOT EXISTS layouts
(
    id       INTEGER NOT NULL,
    chart    TEXT    NOT NULL,
    name     TEXT    NOT NULL,
    type     TEXT    NOT NULL,
    static   BOOLEAN NOT NULL DEFAULT 0,
    width    INTEGER NOT NULL,
    height   INTEGER NOT NULL,
    position INTEGER NOT NULL,
    icon     TEXT,
    sources  TEXT    NOT NULL,
    extras   TEXT    NOT NULL,
    UNIQUE (id, chart),
    FOREIGN KEY (id) REFERENCES projects (id) ON DELETE CASCADE
)