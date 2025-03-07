SELECT project.id            AS project_id,
       project.owner         AS project_owner,
       project.name          AS project_name,
       project.slug          AS project_slug,
       project.private       AS project_private,
       project.icon          AS project_icon,
       project.preview_chart AS project_preview_chart,
       project.url           AS project_url,
       layout.id             AS layout_id,
       layout.chart          AS layout_chart,
       layout.name           AS layout_name,
       layout.type           AS layout_type,
       layout.color          AS layout_color,
       layout.width          AS layout_width,
       layout.height         AS layout_height,
       layout.position       AS layout_position,
       layout.icon           AS layout_icon
FROM (SELECT *
      FROM projects
      WHERE owner = coalesce(?, owner)
        AND private = coalesce(?, private)
      LIMIT ? OFFSET ?) project
         LEFT JOIN layouts layout ON layout.id = project.id AND layout.chart = project.preview_chart