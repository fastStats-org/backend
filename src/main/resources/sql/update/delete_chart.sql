DELETE
FROM layouts
WHERE id = ?
  AND EXISTS (SELECT 1 FROM projects WHERE projects.owner = coalesce(?, projects.owner) AND projects.id = layouts.id);