SELECT COUNT(*)
FROM projects
WHERE owner = COALESCE(?, owner);