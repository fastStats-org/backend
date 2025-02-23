CREATE TABLE IF NOT EXISTS layouts
(
    id    INTEGER PRIMARY KEY NOT NULL,
    name  TEXT                NOT NULL,
    type  TEXT                NOT NULL,
    color TEXT                NOT NULL,
    icon  TEXT,
    size  INTEGER,
    FOREIGN KEY (id) REFERENCES projects (id) ON DELETE CASCADE
)