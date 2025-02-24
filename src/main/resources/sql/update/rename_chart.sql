UPDATE layouts
SET name = ?
WHERE chart = ?
  AND id = ?
  AND exists (SELECT 1 FROM projects WHERE projects.owner = ? AND projects.id = layouts.id);