UPDATE layouts
SET icon = ?
WHERE chart = ?
  AND id = ?
  AND EXISTS (SELECT 1 FROM projects WHERE projects.owner = ? AND projects.id = layouts.id);