INSERT INTO servers (uuid)
VALUES (?)
ON CONFLICT (uuid) DO UPDATE SET uuid = excluded.uuid
RETURNING id