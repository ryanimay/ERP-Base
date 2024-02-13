-- 預設role
INSERT INTO role (role_name, level)
SELECT role_name, level
FROM (VALUES ('visitor', 1),
             ('Basic', 1),
             ('Manager', 2),
             ('Senior', 3)
     ) AS source(role_name, level)
WHERE NOT EXISTS(SELECT 1 FROM role WHERE source.role_name = role.role_name);

-- 預設role權限
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT id FROM role WHERE role_name = 'Basic') AS role_id,
       permission.id                                    AS permission_id
FROM permission
WHERE NOT EXISTS(
        SELECT 1
        FROM role_permission
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'Basic')
          AND permission_id = permission.id
    );

INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT id FROM role WHERE role_name = 'Manager') AS role_id,
       permission.id                                    AS permission_id
FROM permission
WHERE NOT EXISTS(
        SELECT 1
        FROM role_permission
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'Manager')
          AND permission_id = permission.id
    );

INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT id FROM role WHERE role_name = 'Senior') AS role_id,
       permission.id                                    AS permission_id
FROM permission
WHERE NOT EXISTS(
        SELECT 1
        FROM role_permission
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'Senior')
          AND permission_id = permission.id
    );