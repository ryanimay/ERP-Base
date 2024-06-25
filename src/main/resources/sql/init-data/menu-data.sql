-- 預設菜單
INSERT INTO menu (name, path, icon, level, order_num, status, parent_id)
SELECT name, path, icon, level, order_num, status, parent_id
FROM (VALUES
          ('router.clientManagement', null, 'User', 0, 1, 'true', null),
          ('router.permissionManagement', null, 'Key', 0, 2, 'true', null),
          ('router.salaryManagement', null, 'Wallet', 0, 3, 'true', null),
          ('router.performanceManagement', null, 'Trophy', 0, 4, 'true', null),
          ('router.leaveManagement', null, 'Calendar', 0, 5, 'true', null),
          ('router.projectManagement', null, 'Briefcase', 0, 6, 'true', null),
          ('router.procureManagement', null, 'ShoppingCart', 0, 7, 'true', null),
          ('router.jobManagement', null, 'EditPen', 0, 8, 'true', null),
          ('router.scheduleManagement', null, 'Clock', 0, 9, 'true', null),
          ('router.client', 'client', 'List', 1, 1, 'true', 1),
          ('router.department', 'department', 'Grid', 1, 2, 'true', 1)
     ) AS source (name, path, icon, level, order_num, status, parent_id)
WHERE NOT EXISTS(SELECT 1 FROM menu WHERE source.name = menu.name);