UPDATE layouts
SET position = position + 1
WHERE id = ?
  AND position >= ?
  AND chart != ?
  AND EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, projects.owner) AND projects.id = layouts.id)