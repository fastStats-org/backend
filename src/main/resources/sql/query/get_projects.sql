SELECT project.*, layout.*
FROM (SELECT *
      FROM projects
      WHERE owner = coalesce(?, owner)
        AND private = coalesce(?, private)
      LIMIT ? OFFSET ?) project
         LEFT JOIN layouts layout
                   ON layout.id = project.id
                       AND layout.chart = project.preview_chart