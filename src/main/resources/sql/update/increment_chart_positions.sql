UPDATE layouts
SET position = position + 1
WHERE id = ?
  AND position >= ?
  AND chart != ?