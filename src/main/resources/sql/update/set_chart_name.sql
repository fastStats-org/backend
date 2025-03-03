UPDATE layouts
SET name = ?
WHERE chart = ?
  AND id = ?
  AND EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, projects.owner) AND projects.id = layouts.id);