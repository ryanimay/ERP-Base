-- 預設notification
INSERT INTO notification (info, router, status, global, create_time, create_by)
SELECT info, router, status, global, create_time, create_by
FROM (VALUES
    ('notification.sysInit', null, 'true', 'true', GETDATE(), 0)
    ) AS source (info, router, status, global, create_time, create_by)
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE source.info = notification.info);