-- 預設permission
INSERT INTO permission (authority, info, url, status)
SELECT authority, info, url, status
FROM (VALUES ('*', '用戶:測試接口', '/client/opValid', 'true'),
             ('*', '用戶:註冊接口', '/client/register', 'true'),
             ('*', '用戶:登入接口', '/client/login', 'true'),
             ('*', '用戶:重置密碼', '/client/resetPassword', 'true'),
             ('*', '角色:角色/部門列表', '/role/list', 'true'),
             ('CLIENT_LIST', '用戶:用戶列表', '/client/list', 'true'),
             ('CLIENT_GETCLIENT', '用戶:單一用戶', '/client/getClient', 'true'),
             ('CLIENT_UPDATE', '用戶:更新用戶', '/client/update', 'true'),
             ('CLIENT_UPDATEPASSWORD', '用戶:更新用戶密碼', '/client/updatePassword', 'true'),
             ('CLIENT_CLIENTLOCK', '用戶:用戶鎖定', '/client/clientLock', 'true'),
             ('CLIENT_CLIENTSTATUS', '用戶:用戶狀態', '/client/clientStatus', 'true'),
             ('CLIENT_NAMELIST', '用戶:名稱列表', '/client/nameList', 'true'),
             ('ROLE_UPDATE', '角色:更新角色', '/role/update', 'true'),
             ('ROLE_ADD', '角色:新增角色', '/role/add', 'true'),
             ('ROLE_REMOVE', '角色:移除角色', '/role/remove', 'true'),
             ('ROLE_ROLEPERMISSION', '角色:角色對應權限', '/role/rolePermission', 'true'),
             ('ROLE_ROLEROUTER', '角色:角色對應路由', '/role/roleRouter', 'true'),
             ('PERMISSION_ROLE', '權限:權限對應角色', '/permission/role', 'true'),
             ('PERMISSION_LIST', '權限:權限清單', '/permission/list', 'true'),
             ('PERMISSION_BAN', '權限:關閉權限', '/permission/ban', 'true'),
             ('PERMISSION_SECURITYCONFIRM', '權限:權限安全認證', '/permission/securityConfirm', 'true'),
             ('ROUTER_CONFIGLIST', '路由:設定結構清單', '/router/configList', 'true'),
             ('ROUTER_LIST', '路由:路由清單', '/router/list', 'true'),
             ('ROUTER_ROLE', '路由:路由對應角色', '/router/role', 'true'),
             ('SALARY_ROOTS', '薪資:薪資設定清單', '/salary/roots', 'true'),
             ('SALARY_ROOTBY', '薪資:單一薪資設定', '/salary/rootBy', 'true'),
             ('SALARY_EDITROOT', '薪資:編輯薪資根', '/salary/editRoot', 'true'),
             ('SALARY_GET', '薪資:用戶薪資清單', '/salary/get', 'true'),
             ('SALARY_INFO', '薪資:用戶薪資詳細', '/salary/info', 'true'),
             ('PERFORMANCE_PENDINGLIST', '績效:待審核清單', '/performance/pendingList', 'true'),
             ('PERFORMANCE_ALLLIST', '績效:全部清單', '/performance/allList', 'true'),
             ('PERFORMANCE_LIST', '績效:個人績效清單', '/performance/list', 'true'),
             ('PERFORMANCE_ADD', '績效:新增績效', '/performance/add', 'true'),
             ('PERFORMANCE_UPDATE', '績效:更新績效', '/performance/update', 'true'),
             ('PERFORMANCE_REMOVE', '績效:移除績效', '/performance/remove', 'true'),
             ('PERFORMANCE_ACCEPT', '績效:績效審核', '/performance/accept', 'true'),
             ('ATTEND_SIGNIN', '打卡:簽到', '/attend/signIn', 'true'),
             ('ATTEND_SIGNOUT', '打卡:簽退', '/attend/signOut', 'true'),
             ('LEAVE_PENDINGLIST', '休假:待審核清單', '/leave/pendingList', 'true'),
             ('LEAVE_LIST', '休假:角色休假清單', '/leave/list', 'true'),
             ('LEAVE_ADD', '休假:新增休假', '/leave/add', 'true'),
             ('LEAVE_UPDATE', '休假:更新休假', '/leave/update', 'true'),
             ('LEAVE_DELETE', '休假:移除休假申請', '/leave/delete', 'true'),
             ('LEAVE_ACCEPT', '休假:休假審核', '/leave/accept', 'true'),
             ('PROJECT_LIST', '專案:清單', '/project/list', 'true'),
             ('PROJECT_ADD', '專案:新增專案', '/project/add', 'true'),
             ('PROJECT_UPDATE', '專案:更新專案', '/project/update', 'true'),
             ('PROJECT_START', '專案:專案啟動', '/project/start', 'true'),
             ('PROJECT_DONE', '專案:專案結案', '/project/done', 'true'),
             ('PROCUREMENT_LIST', '採購:採購清單', '/procurement/list', 'true'),
             ('PROCUREMENT_ADD', '採購:新增採購紀錄', '/procurement/add', 'true'),
             ('PROCUREMENT_UPDATE', '採購:更新採購紀錄', '/procurement/update', 'true'),
             ('JOB_LIST', '任務:任務清單', '/job/list', 'true'),
             ('JOB_ADD', '任務:新增任務', '/job/add', 'true'),
             ('JOB_UPDATE', '任務:更新任務', '/job/update', 'true'),
             ('JOB_REMOVE', '任務:移除任務', '/job/remove', 'true'),
             ('CACHE_FRESH', '緩存:刷新', '/cache/refresh', 'true')) AS source (authority, info, url, status)
WHERE NOT EXISTS(SELECT 1 FROM permission WHERE source.url = permission.url);

-- 預設notification
INSERT INTO notification (info, router, status, global, create_time, create_by)
SELECT info, router, status, global, create_time, create_by
FROM (VALUES
    ('系統初始化', null, 'true', 'true', GETDATE(), 0)
    ) AS source (info, router, status, global, create_time, create_by)
WHERE NOT EXISTS (SELECT 1 FROM notification WHERE source.info = notification.info);

-- 預設role
INSERT INTO role (role_name, level)
SELECT role_name
FROM (VALUES ('visitor', 1),
             ('super1', 1),
             ('super2', 2)) AS source(role_name)
WHERE NOT EXISTS(SELECT 1 FROM role WHERE source.role_name = role.role_name);

-- 預設super權限
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT id FROM role WHERE role_name = 'super1' OR role_name = 'super2') AS role_id,
       permission.id                                                            AS permission_id
FROM permission
WHERE NOT EXISTS(
        SELECT 1
        FROM role_permission
        WHERE role_id = (SELECT id FROM role WHERE role_name = 'super')
          AND permission_id = permission.id
    );