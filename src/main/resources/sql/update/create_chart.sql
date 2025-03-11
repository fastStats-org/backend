INSERT INTO layouts (id, chart, name, type, static, width, height, position, icon, sources, extras)
SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
WHERE EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, owner) AND projects.id = ?)
