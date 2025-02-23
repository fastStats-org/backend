UPDATE projects
SET private       = coalesce(?, private),
    icon          = coalesce(?, icon),
    preview_chart = coalesce(?, preview_chart),
    url           = coalesce(?, url),
    slug          = coalesce(?, slug)
WHERE id = ? AND owner = coalesce(?, owner);

INSERT INTO layouts (id, name, type, color, icon, size)
VALUES (?, ?, ?, ?, ?, ?)
ON CONFLICT(id, name) DO UPDATE
    SET name  = excluded.name,
        type  = excluded.type,
        color = excluded.color,
        icon  = excluded.icon,
        size  = excluded.size;