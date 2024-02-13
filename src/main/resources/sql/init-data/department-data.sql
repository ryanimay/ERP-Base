-- 預設部門
INSERT INTO department (name, default_role)
SELECT name, default_role
FROM (VALUES ('departmentA', 2),
             ('departmentB', 2),
             ('departmentC', 2)) AS source(name, default_role)
WHERE NOT EXISTS(SELECT 1 FROM department WHERE source.name = department.name);