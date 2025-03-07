CREATE TABLE IF NOT EXISTS layouts
(
    id       INTEGER NOT NULL,
    chart    TEXT    NOT NULL,
    name     TEXT    NOT NULL,
    type     TEXT    NOT NULL,
    color    TEXT    NOT NULL,
    width    INTEGER NOT NULL,
    height   INTEGER NOT NULL,
    position INTEGER NOT NULL,
    icon     TEXT,
    UNIQUE (id, chart),
    FOREIGN KEY (id) REFERENCES projects (id) ON DELETE CASCADE
)