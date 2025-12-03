insert into yongwang_db.yw_role (id, uid, name, code, description, sort, status, create_time, update_time, create_by, update_by, deleted)
values  (1, 'role001', '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 0, 1, '2025-11-29 19:01:26', '2025-11-29 19:01:26', null, null, 0),
        (2, 'role002', '运营人员', 'OPERATOR', '商品和订单管理', 0, 1, '2025-11-29 19:01:26', '2025-11-29 19:01:26', null, null, 0),
        (3, 'role003', '客服人员', 'CUSTOMER_SERVICE', '订单和售后处理', 0, 1, '2025-11-29 19:01:26', '2025-11-29 19:01:26', null, null, 0);