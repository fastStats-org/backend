CREATE TABLE IF NOT EXISTS layout_options
(
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT NOT NULL,
    type  TEXT NOT NULL,
    color TEXT NOT NULL,
    icon  TEXT,
    size  INTEGER
)