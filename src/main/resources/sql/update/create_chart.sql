INSERT INTO layouts (id, chart, name, type, color, width, height, position, icon)
SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?
WHERE EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, owner) AND projects.id = ?)
