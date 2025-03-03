SELECT *
FROM projects
WHERE slug = ?
  AND (private = false OR owner = coalesce(?, owner))