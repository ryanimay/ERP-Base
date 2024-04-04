-- 預設菜單
INSERT INTO menu (name, path, icon, level, order_num, status, parent_id)
SELECT name, path, icon, level, order_num, status, parent_id
FROM (VALUES
          ('用戶管理', null, 'User', 0, 1, 'true', null),
          ('權限管理', null, 'Key', 0, 2, 'true', null),
          ('薪資管理', null, 'Wallet', 0, 3, 'true', null),
          ('績效管理', null, 'Trophy', 0, 4, 'true', null),
          ('休假管理', null, 'Calendar', 0, 5, 'true', null),
          ('專案管理', null, 'Briefcase', 0, 6, 'true', null),
          ('採購管理', null, 'ShoppingCart', 0, 7, 'true', null),
          ('任務管理', null, 'EditPen', 0, 8, 'true', null),
          ('排程管理', null, 'Clock', 0, 9, 'true', null),
          ('用戶清單', 'client', 'List', 1, 1, 'true', 1)
     ) AS source (name, path, icon, level, order_num, status, parent_id)
WHERE NOT EXISTS(SELECT 1 FROM menu WHERE source.name = menu.name);