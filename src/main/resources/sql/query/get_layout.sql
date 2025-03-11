SELECT layout.id       AS layout_id,
       layout.chart    AS layout_chart,
       layout.name     AS layout_name,
       layout.type     AS layout_type,
       layout.width    AS layout_width,
       layout.height   AS layout_height,
       layout.position AS layout_position,
       layout.icon     AS layout_icon,
       layout.sources  AS layout_sources,
       layout.extras   AS layout_extras
FROM layouts layout
WHERE layout.id = ?