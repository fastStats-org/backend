SELECT * FROM projects
WHERE owner = coalesce(?, owner)
  AND private = coalesce(?, private)
LIMIT ? OFFSET ?