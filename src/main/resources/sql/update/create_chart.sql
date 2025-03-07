INSERT INTO layouts ("id", "chart", "name", "type", "color", "width", "height", "index", "icon")
SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?
WHERE EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, owner) AND projects.id = ?)
