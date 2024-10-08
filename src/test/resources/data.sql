-- 預設permission
INSERT INTO permission (authority, info, url, status)
SELECT authority, info, url, status
FROM (VALUES ('*', '用戶:測試接口', '/client/opValid', 'true'),
             ('*', '用戶:註冊接口', '/client/register', 'true'),
             ('*', '用戶:登入接口', '/client/login', 'true'),
             ('*', '用戶:重置密碼', '/client/resetPassword', 'true'),
             ('*', '用戶:登出', '/client/logout', 'true'),
             ('CLIENT_LIST', '用戶:用戶列表', '/client/list', 'true'),
             ('CLIENT_GETCLIENT', '用戶:單一用戶', '/client/getClient', 'true'),
             ('CLIENT_UPDATE', '用戶:更新用戶', '/client/update', 'true'),
             ('CLIENT_UPDATEPASSWORD', '用戶:更新用戶密碼', '/client/updatePassword', 'true'),
             ('CLIENT_CLIENTLOCK', '用戶:用戶鎖定', '/client/clientLock', 'true'),
             ('CLIENT_CLIENTSTATUS', '用戶:用戶狀態', '/client/clientStatus', 'true'),
             ('CLIENT_NAMELIST', '用戶:名稱列表', '/client/nameList', 'true'),
             ('CLIENT_REFRESH', '用戶:刷新Token', '/client/refreshT', 'true'),
             ('CLIENT_SYSTEMINFO', '用戶:系統資訊', '/client/systemInfo', 'true'),
             ('ROLE_LIST', '角色:角色列表', '/role/list', 'true'),
             ('ROLE_UPDATE', '角色:更新角色', '/role/update', 'true'),
             ('ROLE_ADD', '角色:新增角色', '/role/add', 'true'),
             ('ROLE_REMOVE', '角色:移除角色', '/role/remove', 'true'),
             ('ROLE_ROLEPERMISSION', '角色:角色對應權限', '/role/rolePermission', 'true'),
             ('ROLE_ROLEROUTER', '角色:角色對應路由', '/role/roleRouter', 'true'),
             ('ROLE_MENU', '角色:角色對應菜單', '/role/roleMenu', 'true'),
             ('PERMISSION_ROLE', '權限:權限對應角色', '/permission/role', 'true'),
             ('PERMISSION_LIST', '權限:權限清單', '/permission/list', 'true'),
             ('PERMISSION_BAN', '權限:關閉權限', '/permission/ban', 'true'),
             ('PERMISSION_SECURITYCONFIRM', '權限:權限安全認證', '/permission/securityConfirm', 'true'),
             ('PERMISSION_GETKEY', '權限:獲取公鑰', '/permission/getKey', 'true'),
             ('MENU_ALL', '菜單:全部菜單', '/menu/all', 'true'),
             ('MENU_PMENU', '菜單:用戶菜單', '/menu/pMenu', 'true'),
             ('SALARY_ROOTS', '薪資:薪資設定清單', '/salary/roots', 'true'),
             ('SALARY_EDITROOT', '薪資:編輯薪資根', '/salary/editRoot', 'true'),
             ('SALARY_GET', '薪資:用戶薪資清單', '/salary/get', 'true'),
             ('SALARY_INFO', '薪資:用戶薪資詳細', '/salary/info', 'true'),
             ('PERFORMANCE_PENDINGLIST', '績效:待審核清單', '/performance/pendingList', 'true'),
             ('PERFORMANCE_LIST', '績效:績效清單', '/performance/list', 'true'),
             ('PERFORMANCE_ADD', '績效:新增績效', '/performance/add', 'true'),
             ('PERFORMANCE_UPDATE', '績效:更新績效', '/performance/update', 'true'),
             ('PERFORMANCE_REMOVE', '績效:移除績效', '/performance/remove', 'true'),
             ('PERFORMANCE_ACCEPT', '績效:績效審核', '/performance/accept', 'true'),
             ('PERFORMANCE_CALCULATE', '績效:年度結算', '/performance/calculate', 'true'),
             ('ATTEND_SIGNIN', '打卡:簽到', '/attend/signIn', 'true'),
             ('ATTEND_SIGNOUT', '打卡:簽退', '/attend/signOut', 'true'),
             ('LEAVE_PENDINGLIST', '休假:待審核清單', '/leave/pendingList', 'true'),
             ('LEAVE_LIST', '休假:角色休假清單', '/leave/list', 'true'),
             ('LEAVE_ADD', '休假:新增休假', '/leave/add', 'true'),
             ('LEAVE_UPDATE', '休假:更新休假', '/leave/update', 'true'),
             ('LEAVE_DELETE', '休假:移除休假申請', '/leave/delete', 'true'),
             ('LEAVE_ACCEPT', '休假:休假審核', '/leave/accept', 'true'),
             ('LEAVE_REJECT', '休假:休假駁回', '/leave/reject', 'true'),
             ('LEAVE_TYPELIST', '休假:假別清單', '/leave/typeList', 'true'),
             ('PROJECT_LIST', '專案:清單', '/project/list', 'true'),
             ('PROJECT_ADD', '專案:新增專案', '/project/add', 'true'),
             ('PROJECT_UPDATE', '專案:更新專案', '/project/update', 'true'),
             ('PROJECT_START', '專案:專案啟動', '/project/start', 'true'),
             ('PROJECT_DONE', '專案:專案結案', '/project/done', 'true'),
             ('PROJECT_ORDER', '專案:專案排序', '/project/order', 'true'),
             ('PROCUREMENT_LIST', '採購:採購清單', '/procurement/list', 'true'),
             ('PROCUREMENT_ADD', '採購:新增採購紀錄', '/procurement/add', 'true'),
             ('PROCUREMENT_UPDATE', '採購:更新採購紀錄', '/procurement/update', 'true'),
             ('PROCUREMENT_DELETE', '採購:刪除採購紀錄', '/procurement/delete', 'true'),
             ('JOB_LIST', '任務:任務清單', '/job/list', 'true'),
             ('JOB_ADD', '任務:新增任務', '/job/add', 'true'),
             ('JOB_UPDATE', '任務:更新任務', '/job/update', 'true'),
             ('JOB_REMOVE', '任務:移除任務', '/job/remove', 'true'),
             ('CACHE_FRESH', '緩存:刷新', '/cache/refresh', 'true'),
             ('DEPARTMENT_LIST', '部門:清單', '/department/list', 'true'),
             ('DEPARTMENT_STAFF', '部門:員工', '/department/staff', 'true'),
             ('DEPARTMENT_EDIT', '部門:編輯', '/department/edit', 'true'),
             ('DEPARTMENT_REMOVE', '部門:移除', '/department/remove', 'true'),
             ('QUARTZ_LIST', '排程:排程清單', '/quartzJob/list', 'true'),
             ('QUARTZ_ADD', '排程:新增排程任務', '/quartzJob/add', 'true'),
             ('QUARTZ_UPDATE', '排程:更新排程任務', '/quartzJob/update', 'true'),
             ('QUARTZ_TOGGLE', '排程:切換排程狀態', '/quartzJob/toggle', 'true'),
             ('QUARTZ_DELETE', '排程:刪除排程', '/quartzJob/delete', 'true'),
             ('QUARTZ_EXEC', '排程:執行一次排程任務', '/quartzJob/exec', 'true'),
             ('LOG_LIST', '日誌:清單', '/log/list', 'true'),
             ('NOTIFICATION_STATUS', '通知:更改狀態', '/notification/status', 'true')
             ) AS source (authority, info, url, status)
WHERE NOT EXISTS(SELECT 1 FROM permission WHERE source.url = permission.url);

-- 預設notification
INSERT INTO notification (info, router, status, global, create_time, create_by)
SELECT info, router, status, global, create_time, create_by
FROM (VALUES
    ('notification.sysInit', null, 'true', 'true', CURRENT_TIMESTAMP, 0)
    ) AS source (info, router, status, global, create_time, create_by)
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE source.info = notification.info);

-- 預設role
INSERT INTO role (role_name, level)
SELECT role_name, level
FROM (VALUES ('visitor', 1),
             ('Basic', 1),
             ('Manager', 2),
             ('Senior', 3)
             ) AS source(role_name, level)
WHERE NOT EXISTS(SELECT 1 FROM role WHERE source.role_name = role.role_name);

-- 預設權限
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

-- 預設部門
INSERT INTO department (name, default_role)
SELECT name, default_role
FROM (VALUES ('departmentA', 2),
             ('departmentB', 2),
             ('departmentC', 2)) AS source(name, default_role)
WHERE NOT EXISTS(SELECT 1 FROM department WHERE source.name = department.name);

-- 預設部門
INSERT INTO annual_leave (current_leave, total_leave)
SELECT current_leave, total_leave
FROM (VALUES ('1', '5')) AS source(current_leave, total_leave)
WHERE NOT EXISTS(SELECT 1 FROM annual_leave WHERE source.current_leave = annual_leave.current_leave);

--模擬登入用戶資料
INSERT INTO client(username, password, is_active, is_lock, email, create_by, create_time, must_update_password, attend_status, department_id, annual_leave_id)
SELECT username, password, is_active, is_lock, email, create_by, create_time, must_update_password, attend_status, department_id, annual_leave_id
FROM (VALUES
          ('test', 'test', true, false, 'testMail@gmail.com', 0, CURRENT_TIMESTAMP, false, 1, 1, 1)
      ) AS source(username, password, is_active, is_lock, email, create_by, create_time, must_update_password, attend_status, department_id, annual_leave_id)
WHERE NOT EXISTS(SELECT 1 FROM client WHERE source.username = client.username);

INSERT INTO client_roles(client_id, role_id)
SELECT client_id, role_id
FROM (VALUES (1, 2)) AS source(client_id, role_id)
WHERE NOT EXISTS(SELECT 1 FROM client_roles WHERE source.client_id = client_roles.client_id AND source.role_id = client_roles.role_id);

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
          ('router.scheduleManagement', null, 'Clock', 0, 9, 'true', null)
     ) AS source (name, path, icon, level, order_num, status, parent_id)
WHERE NOT EXISTS(SELECT 1 FROM menu WHERE source.name = menu.name);

INSERT INTO menu (name, path, icon, level, order_num, status, parent_id)
SELECT name, path, icon, level, order_num, status, parent_id
FROM (VALUES
          ('用戶清單', 'client', 'List', 1, 1, true,
           (SELECT id FROM menu WHERE name = 'router.clientManagement')
          ),
          ('test', 'client', 'List', 1, 2, true,
           (SELECT id FROM menu WHERE name = 'router.permissionManagement')
          ),
          ('test', 'client', 'List', 1, 3, true,
           (SELECT id FROM menu WHERE name = 'router.salaryManagement')
          ),
          ('test', 'client', 'List', 1, 4, true,
           (SELECT id FROM menu WHERE name = 'router.performanceManagement')
          ),
          ('test', 'client', 'List', 1, 5, true,
           (SELECT id FROM menu WHERE name = 'router.leaveManagement')
          ),
          ('test', 'client', 'List', 1, 6, true,
           (SELECT id FROM menu WHERE name = 'router.projectManagement')
          ),
          ('test', 'client', 'List', 1, 7, true,
           (SELECT id FROM menu WHERE name = 'router.procureManagement')
          ),
          ('test', 'client', 'List', 1, 8, true,
           (SELECT id FROM menu WHERE name = 'router.jobManagement')
          ),
          ('test', 'client', 'List', 1, 9, true,
           (SELECT id FROM menu WHERE name = 'router.scheduleManagement')
          )
     ) AS source (name, path, icon, level, order_num, status, parent_id)
WHERE NOT EXISTS(SELECT 1 FROM menu WHERE source.name = menu.name);

-- 預設role菜單
INSERT INTO menu_role (role_id, menu_id)
SELECT (SELECT id FROM role WHERE role_name = 'Basic') AS role_id,
       menu.id                                    AS menu_id
FROM menu
WHERE NOT EXISTS(
        SELECT 1
        FROM menu_role
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'Basic')
          AND menu_id = menu.id
    );

INSERT INTO menu_role (role_id, menu_id)
SELECT (SELECT id FROM role WHERE role_name = 'Manager') AS role_id,
       menu.id                                    AS menu_id
FROM menu
WHERE NOT EXISTS(
        SELECT 1
        FROM menu_role
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'Manager')
          AND menu_id = menu.id
    );

INSERT INTO menu_role (role_id, menu_id)
SELECT (SELECT id FROM role WHERE role_name = 'Senior') AS role_id,
       menu.id                                    AS menu_id
FROM menu
WHERE NOT EXISTS(
        SELECT 1
        FROM menu_role
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'Senior')
          AND menu_id = menu.id
    );