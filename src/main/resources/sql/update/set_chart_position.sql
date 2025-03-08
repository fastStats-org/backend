UPDATE layouts
SET position = position + 1
WHERE id = ?
  AND position >= ?
  AND chart != ?;

UPDATE layouts
SET position = ?
WHERE chart = ?
  AND id = ?
  AND EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, projects.owner) AND projects.id = layouts.id);