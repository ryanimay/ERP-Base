-- 開啟自訂主鍵
SET IDENTITY_INSERT menu ON;

-- 預設菜單root
INSERT INTO menu (id, name, path, icon, level, order_num, status, parent_id)
SELECT id, name, path, icon, level, order_num, status, parent_id
FROM (VALUES
          (1, 'router.clientManagement', null, 'User', 0, 1, 'true', null),
          (2, 'router.permissionManagement', null, 'Key', 0, 2, 'true', null),
          (3, 'router.salaryManagement', null, 'Wallet', 0, 3, 'true', null),
          (4, 'router.performanceManagement', null, 'Trophy', 0, 4, 'true', null),
          (5, 'router.leaveManagement', null, 'Calendar', 0, 5, 'true', null),
          (6, 'router.projectManagement', null, 'Briefcase', 0, 6, 'true', null),
          (7, 'router.procureManagement', null, 'ShoppingCart', 0, 7, 'true', null),
          (8, 'router.jobManagement', null, 'EditPen', 0, 8, 'true', null),
          (9, 'router.scheduleManagement', null, 'Clock', 0, 9, 'true', null)
     ) AS source (id, name, path, icon, level, order_num, status, parent_id)
WHERE NOT EXISTS(SELECT 1 FROM menu WHERE source.name = menu.name);
-- 關閉自訂主鍵
SET IDENTITY_INSERT menu OFF;

-- 預設子菜單
INSERT INTO menu (name, path, icon, level, order_num, status, parent_id)
SELECT name, path, icon, level, order_num, status, parent_id
FROM (VALUES
          ('router.client', 'client', 'List', 1, 1, 'true', 1),
          ('router.department', 'department', 'Grid', 1, 1, 'true', 2),
          ('router.rolePermission', 'rolePermission', 'Finished', 1, 2, 'true', 2),
          ('router.roleMenu', 'roleMenu', 'Guide', 1, 3, 'true', 2),
          ('router.salaryList', 'salaryList', 'WalletFilled', 1, 1, 'true', 3),
          ('router.salarySetting', 'salarySetting', 'MagicStick', 1, 2, 'true', 3),
          ('router.personalPerformance', 'personalPerformance', 'GoldMedal', 1, 1, 'true', 4),
          ('router.performanceList', 'performanceList', 'Star', 1, 2, 'true', 4),
          ('router.personalLeave', 'personalLeave', 'AlarmClock', 1, 1, 'true', 5),
          ('router.leaveList', 'leaveList', 'Drizzling', 1, 2, 'true', 5),
          ('router.projectList', 'projectList', 'Goods', 1, 1, 'true', 6),
          ('router.procureList', 'procureList', 'ShoppingCartFull', 1, 1, 'true', 7),
          ('router.jobCard', 'jobCard', 'Ticket', 1, 1, 'true', 8),
          ('router.scheduleList', 'scheduleList', 'Timer', 1, 1, 'true', 9)
     ) AS source (name, path, icon, level, order_num, status, parent_id)
WHERE NOT EXISTS(SELECT 1 FROM menu WHERE source.name = menu.name);
