SELECT layout.id       AS layout_id,
       layout.chart    AS layout_chart,
       layout.name     AS layout_name,
       layout.type     AS layout_type,
       layout.color    AS layout_color,
       layout.width    AS layout_width,
       layout.height   AS layout_height,
       layout.position AS layout_position,
       layout.icon     AS layout_icon
FROM layouts layout
WHERE layout.id = ?