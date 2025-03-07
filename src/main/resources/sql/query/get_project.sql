SELECT project.id            AS project_id,
       project.owner         AS project_owner,
       project.name          AS project_name,
       project.slug          AS project_slug,
       project.private       AS project_private,
       project.icon          AS project_icon,
       project.preview_chart AS project_preview_chart,
       project.url           AS project_url
FROM projects project
WHERE slug = ?
  AND (private = false OR owner = coalesce(?, owner))