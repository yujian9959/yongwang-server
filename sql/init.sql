-- ============================================
-- 永旺农资电商系统数据库初始化脚本
-- 数据库：yongwang_db
-- 字符集：utf8mb4
-- ============================================

CREATE DATABASE IF NOT EXISTS yongwang_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yongwang_db;


create table yw_admin
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    uid             varchar(32)                        not null comment '业务主键',
    username        varchar(50)                        not null comment '用户名',
    password        varchar(100)                       not null comment '密码(加密)',
    real_name       varchar(50)                        null comment '真实姓名',
    phone           varchar(20)                        null comment '手机号',
    email           varchar(100)                       null comment '邮箱',
    avatar          varchar(255)                       null comment '头像URL',
    role_uid        varchar(32)                        null comment '角色UID',
    status          tinyint  default 1                 null comment '状态：0禁用 1启用',
    last_login_time datetime                           null comment '最后登录时间',
    last_login_ip   varchar(50)                        null comment '最后登录IP',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by       varchar(32)                        null comment '创建人',
    update_by       varchar(32)                        null comment '更新人',
    deleted         tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_uid
        unique (uid),
    constraint uk_username
        unique (username)
)
    comment '管理员表' charset = utf8mb4;

create index idx_role_uid
    on yw_admin (role_uid);

create index idx_status
    on yw_admin (status);

create table yw_agri_article
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    title       varchar(200)                       not null comment '文章标题',
    summary     varchar(500)                       null comment '文章摘要',
    cover_image varchar(255)                       null comment '封面图片',
    content     text                               null comment '文章内容(富文本)',
    category    varchar(50)                        null comment '文章分类',
    view_count  int      default 0                 null comment '阅读量',
    like_count  int      default 0                 null comment '点赞数',
    sort        int      default 0                 null comment '排序',
    status      tinyint  default 0                 null comment '状态：0草稿 1已发布',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '农技文章表' charset = utf8mb4;

create index idx_category
    on yw_agri_article (category);

create index idx_status
    on yw_agri_article (status);

create table yw_agri_qa
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    user_uid    varchar(32)                        null comment '提问用户UID',
    title       varchar(200)                       not null comment '问题标题',
    content     text                               null comment '问题内容',
    images      varchar(1000)                      null comment '问题图片(JSON数组)',
    category    varchar(50)                        null comment '问题分类',
    answer      text                               null comment '回答内容',
    answer_by   varchar(32)                        null comment '回答人UID',
    answer_time datetime                           null comment '回答时间',
    view_count  int      default 0                 null comment '浏览量',
    status      tinyint  default 0                 null comment '状态：0待回答 1已回答',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '农技问答表' charset = utf8mb4;

create index idx_category
    on yw_agri_qa (category);

create index idx_status
    on yw_agri_qa (status);

create index idx_user_uid
    on yw_agri_qa (user_uid);

create table yw_agri_task
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                           not null comment '业务主键',
    title       varchar(100)                          not null comment '任务标题',
    description text                                  null comment '任务描述',
    icon        varchar(50)                           null comment '任务图标',
    type        varchar(20)                           null comment '任务类型：planting种植/fertilizing施肥/pest病虫害/harvest收获/other其他',
    priority    varchar(20) default 'medium'          null comment '优先级：high高/medium中/low低',
    crops       varchar(500)                          null comment '涉及作物(JSON数组)',
    task_date   date                                  null comment '任务日期',
    month       int                                   null comment '所属月份(1-12)',
    status      tinyint     default 1                 null comment '状态：0禁用 1启用',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                           null comment '创建人',
    update_by   varchar(32)                           null comment '更新人',
    deleted     tinyint     default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '农事任务表' charset = utf8mb4;

create index idx_month
    on yw_agri_task (month);

create index idx_task_date
    on yw_agri_task (task_date);

create index idx_type
    on yw_agri_task (type);

create table yw_banner
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    title       varchar(100)                       null comment '标题',
    image       varchar(255)                       not null comment '图片URL',
    link_type   tinyint  default 0                 null comment '链接类型：0无 1商品 2分类 3外链',
    link_value  varchar(255)                       null comment '链接值',
    sort        int      default 0                 null comment '排序',
    status      tinyint  default 1                 null comment '状态：0禁用 1启用',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '轮播图表' charset = utf8mb4;

create index idx_sort
    on yw_banner (sort);

create index idx_status
    on yw_banner (status);

create table yw_brand
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    name        varchar(100)                       not null comment '品牌名称',
    logo        varchar(255)                       null comment '品牌Logo',
    description varchar(500)                       null comment '品牌描述',
    sort        int      default 0                 null comment '排序',
    status      tinyint  default 1                 null comment '状态：0禁用 1启用',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '品牌表' charset = utf8mb4;

create index idx_status
    on yw_brand (status);

create table yw_browse_history
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    user_uid    varchar(32)                        not null comment '用户UID',
    goods_uid   varchar(32)                        not null comment '商品UID',
    browse_time datetime default CURRENT_TIMESTAMP null comment '浏览时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_uid
        unique (uid),
    constraint uk_user_goods
        unique (user_uid, goods_uid) comment '同一商品只保留最新一条记录'
)
    comment '浏览记录表' charset = utf8mb4;

create index idx_browse_time
    on yw_browse_history (browse_time);

create index idx_goods_uid
    on yw_browse_history (goods_uid);

create index idx_user_uid
    on yw_browse_history (user_uid);

create table yw_cart
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    user_uid    varchar(32)                        not null comment '用户UID',
    spu_uid     varchar(32)                        not null comment 'SPU UID',
    sku_uid     varchar(32)                        null comment 'SKU UID',
    quantity    int      default 1                 null comment '数量',
    selected    tinyint  default 1                 null comment '是否选中：0否 1是',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(50)                        null comment '创建人',
    update_by   varchar(50)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除：0正常 1删除',
    constraint uk_uid
        unique (uid),
    constraint uk_user_spu
        unique (user_uid, spu_uid)
)
    comment '购物车表' charset = utf8mb4;

create index idx_user_uid
    on yw_cart (user_uid);

create table yw_category
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                           not null comment '业务主键',
    parent_uid  varchar(32) default '0'               null comment '父级UID',
    name        varchar(50)                           not null comment '分类名称',
    icon        varchar(255)                          null comment '分类图标',
    image       varchar(255)                          null comment '分类图片',
    sort        int         default 0                 null comment '排序',
    level       tinyint     default 1                 null comment '层级：1一级 2二级',
    status      tinyint     default 1                 null comment '状态：0禁用 1启用',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                           null comment '创建人',
    update_by   varchar(32)                           null comment '更新人',
    deleted     tinyint     default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '商品分类表' charset = utf8mb4;

create index idx_parent_uid
    on yw_category (parent_uid);

create index idx_sort
    on yw_category (sort);

create index idx_status
    on yw_category (status);

create table yw_coupon
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    uid           varchar(32)                              not null comment '业务主键',
    name          varchar(100)                             not null comment '优惠券名称',
    type          tinyint                                  not null comment '类型：1满减券 2折扣券',
    amount        decimal(10, 2)                           null comment '优惠金额(满减券)',
    discount      decimal(3, 2)                            null comment '折扣率(折扣券)',
    min_amount    decimal(10, 2) default 0.00              null comment '最低消费金额',
    total_count   int            default 0                 null comment '发放总量',
    receive_count int            default 0                 null comment '已领取数量',
    use_count     int            default 0                 null comment '已使用数量',
    per_limit     int            default 1                 null comment '每人限领数量',
    start_time    datetime                                 null comment '开始时间',
    end_time      datetime                                 null comment '结束时间',
    status        tinyint        default 1                 null comment '状态：0禁用 1启用',
    create_time   datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by     varchar(32)                              null comment '创建人',
    update_by     varchar(32)                              null comment '更新人',
    deleted       tinyint        default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '优惠券模板表' charset = utf8mb4;

create index idx_status
    on yw_coupon (status);

create table yw_floor
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    name        varchar(50)                        not null comment '楼层名称',
    type        varchar(20)                        not null comment '楼层类型：seckill秒杀/hot热卖/new新品/recommend推荐',
    icon        varchar(50)                        null comment '楼层图标',
    bg_color    varchar(20)                        null comment '背景颜色',
    show_more   tinyint  default 1                 null comment '是否显示查看更多：0否 1是',
    more_link   varchar(255)                       null comment '查看更多跳转链接',
    goods_count int      default 4                 null comment '展示商品数量',
    sort        int      default 0                 null comment '排序（数值越小越靠前）',
    status      tinyint  default 1                 null comment '状态：0禁用 1启用',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_uid
        unique (uid)
)
    comment '首页楼层配置表' charset = utf8mb4;

create index idx_status_sort
    on yw_floor (status, sort);

create table yw_goods_review
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    uid            varchar(32)                        not null comment '业务主键',
    user_uid       varchar(32)                        not null comment '用户UID',
    goods_uid      varchar(32)                        not null comment '商品UID',
    order_uid      varchar(32)                        not null comment '订单UID',
    order_item_uid varchar(32)                        null comment '订单商品UID',
    rating         tinyint  default 5                 not null comment '评分：1-5星',
    content        text                               null comment '评价内容',
    images         varchar(1000)                      null comment '评价图片(JSON数组，最多9张)',
    is_anonymous   tinyint  default 0                 null comment '是否匿名：0否 1是',
    reply_content  text                               null comment '商家回复内容',
    reply_time     datetime                           null comment '商家回复时间',
    status         tinyint  default 1                 null comment '状态：0隐藏 1显示',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted        tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_order_item
        unique (order_item_uid) comment '一个订单商品只能评价一次',
    constraint uk_uid
        unique (uid)
)
    comment '商品评价表' charset = utf8mb4;

create index idx_create_time
    on yw_goods_review (create_time);

create index idx_goods_uid
    on yw_goods_review (goods_uid);

create index idx_order_uid
    on yw_goods_review (order_uid);

create index idx_rating
    on yw_goods_review (rating);

create index idx_user_uid
    on yw_goods_review (user_uid);

create table yw_goods_spu
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    uid             varchar(32)                           not null comment '业务主键',
    spu_code        varchar(50)                           null comment 'SPU编码',
    category_uid    varchar(32)                           not null comment '分类UID',
    brand_uid       varchar(32)                           null comment '品牌UID',
    name            varchar(200)                          not null comment '商品名称',
    subtitle        varchar(300)                          null comment '副标题',
    main_image      varchar(255)                          null comment '主图URL',
    images          text                                  null comment '商品图片(JSON数组)',
    video_url       varchar(255)                          null comment '视频URL',
    price           decimal(10, 2)                        not null comment '销售价格',
    original_price  decimal(10, 2)                        null comment '原价',
    cost_price      decimal(10, 2)                        null comment '成本价',
    stock           int         default 0                 null comment '总库存',
    sales           int         default 0                 null comment '销量',
    unit            varchar(20) default '件'              null comment '单位',
    weight          decimal(10, 3)                        null comment '重量(kg)',
    registration_no varchar(50)                           null comment '农药/肥料登记证号',
    manufacturer    varchar(200)                          null comment '生产厂家',
    spec_info       varchar(200)                          null comment '规格信息',
    attrs           json                                  null comment '商品属性(JSON)',
    usage_desc      text                                  null comment '使用方法',
    notice          text                                  null comment '注意事项',
    detail          text                                  null comment '商品详情(富文本)',
    status          tinyint     default 0                 null comment '状态：0下架 1上架',
    is_hot          tinyint     default 0                 null comment '是否热销',
    is_new          tinyint     default 0                 null comment '是否新品',
    is_recommend    tinyint     default 0                 null comment '是否推荐',
    sort            int         default 0                 null comment '排序',
    create_time     datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by       varchar(32)                           null comment '创建人',
    update_by       varchar(32)                           null comment '更新人',
    deleted         tinyint     default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '商品SPU表' charset = utf8mb4;

create index idx_brand_uid
    on yw_goods_spu (brand_uid);

create index idx_category_uid
    on yw_goods_spu (category_uid);

create index idx_is_hot
    on yw_goods_spu (is_hot);

create index idx_is_recommend
    on yw_goods_spu (is_recommend);

create index idx_price
    on yw_goods_spu (price);

create index idx_sales
    on yw_goods_spu (sales);

create index idx_status
    on yw_goods_spu (status);

create table yw_order
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    uid              varchar(32)                              not null comment '业务主键',
    order_no         varchar(32)                              not null comment '订单号',
    user_uid         varchar(32)                              not null comment '用户UID',
    total_amount     decimal(10, 2)                           not null comment '商品总金额',
    freight_amount   decimal(10, 2) default 0.00              null comment '运费',
    coupon_amount    decimal(10, 2) default 0.00              null comment '优惠券抵扣',
    points_amount    decimal(10, 2) default 0.00              null comment '积分抵扣',
    discount_amount  decimal(10, 2) default 0.00              null comment '会员折扣',
    pay_amount       decimal(10, 2)                           not null comment '实付金额',
    coupon_uid       varchar(32)                              null comment '使用的优惠券UID',
    use_points       int            default 0                 null comment '使用积分数',
    earn_points      int            default 0                 null comment '获得积分数',
    receiver_name    varchar(50)                              not null comment '收货人',
    receiver_phone   varchar(20)                              not null comment '收货电话',
    receiver_address varchar(300)                             not null comment '收货地址',
    status           varchar(20)    default 'pending'         not null comment '状态：pending待付款/paid待发货/shipped已发货/completed已完成/cancelled已取消',
    pay_type         varchar(20)                              null comment '支付方式：wechat微信',
    pay_time         datetime                                 null comment '支付时间',
    pay_trade_no     varchar(64)                              null comment '支付流水号',
    express_company  varchar(50)                              null comment '物流公司',
    express_no       varchar(50)                              null comment '物流单号',
    ship_time        datetime                                 null comment '发货时间',
    receive_time     datetime                                 null comment '收货时间',
    remark           varchar(500)                             null comment '买家备注',
    admin_remark     varchar(500)                             null comment '商家备注',
    cancel_reason    varchar(200)                             null comment '取消原因',
    cancel_time      datetime                                 null comment '取消时间',
    complete_time    datetime                                 null comment '完成时间',
    create_time      datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by        varchar(32)                              null comment '创建人',
    update_by        varchar(32)                              null comment '更新人',
    deleted          tinyint        default 0                 null comment '逻辑删除',
    cart_uids        varchar(500)                             null comment '关联的购物车项UID列表',
    constraint uk_order_no
        unique (order_no),
    constraint uk_uid
        unique (uid)
)
    comment '订单主表' charset = utf8mb4;

create index idx_create_time
    on yw_order (create_time);

create index idx_pay_time
    on yw_order (pay_time);

create index idx_status
    on yw_order (status);

create index idx_user_uid
    on yw_order (user_uid);

create table yw_order_item
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    uid          varchar(32)                           not null comment '业务主键',
    order_uid    varchar(32)                           not null comment '订单UID',
    spu_uid      varchar(32)                           not null comment 'SPU UID',
    sku_uid      varchar(32)                           null comment 'SKU UID',
    goods_name   varchar(200)                          not null comment '商品名称',
    goods_image  varchar(255)                          null comment '商品图片',
    spec_info    varchar(200)                          null comment '规格信息',
    price        decimal(10, 2)                        not null comment '单价',
    quantity     int                                   not null comment '数量',
    total_amount decimal(10, 2)                        not null comment '小计金额',
    create_time  datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by    varchar(32) default ''                null comment '创建人',
    update_by    varchar(32) default ''                null comment '更新人',
    deleted      tinyint     default 0                 null comment '逻辑删除(0-未删除,1-已删除)',
    constraint uk_uid
        unique (uid)
)
    comment '订单商品表' charset = utf8mb4;

create index idx_order_uid
    on yw_order_item (order_uid);

create table yw_role
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    name        varchar(50)                        not null comment '角色名称',
    code        varchar(50)                        not null comment '角色编码',
    description varchar(200)                       null comment '角色描述',
    sort        int      default 0                 null comment '排序',
    status      tinyint  default 1                 null comment '状态：0禁用 1启用',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除',
    constraint uk_code
        unique (code),
    constraint uk_uid
        unique (uid)
)
    comment '角色表' charset = utf8mb4;

create table yw_seckill_activity
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    name        varchar(100)                       not null comment '活动名称',
    start_time  datetime                           not null comment '活动开始时间',
    end_time    datetime                           not null comment '活动结束时间',
    status      tinyint  default 0                 null comment '状态：0未开始 1进行中 2已结束',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by   varchar(32)                        null comment '创建人',
    update_by   varchar(32)                        null comment '更新人',
    deleted     tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_uid
        unique (uid)
)
    comment '秒杀活动表' charset = utf8mb4;

create index idx_status
    on yw_seckill_activity (status);

create index idx_time
    on yw_seckill_activity (start_time, end_time);

create table yw_seckill_goods
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    uid           varchar(32)                        not null comment '业务主键',
    activity_uid  varchar(32)                        not null comment '秒杀活动UID',
    spu_uid       varchar(32)                        not null comment '商品SPU UID',
    seckill_price decimal(10, 2)                     not null comment '秒杀价格',
    seckill_stock int                                not null comment '秒杀库存',
    sold_count    int      default 0                 null comment '已售数量',
    limit_count   int      default 1                 null comment '每人限购数量',
    sort          int      default 0                 null comment '排序',
    status        tinyint  default 1                 null comment '状态：0禁用 1启用',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by     varchar(32)                        null comment '创建人',
    update_by     varchar(32)                        null comment '更新人',
    deleted       tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_activity_spu
        unique (activity_uid, spu_uid) comment '同一活动同一商品只能参加一次',
    constraint uk_uid
        unique (uid)
)
    comment '秒杀商品表' charset = utf8mb4;

create index idx_activity_uid
    on yw_seckill_goods (activity_uid);

create index idx_spu_uid
    on yw_seckill_goods (spu_uid);

create index idx_status
    on yw_seckill_goods (status);

create table yw_solar_term
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    uid          varchar(32)                        not null comment '业务主键',
    name         varchar(50)                        not null comment '节气名称',
    term_date    date                               not null comment '节气日期',
    description  text                               null comment '节气描述',
    farming_tips text                               null comment '农事建议',
    year         int                                null comment '年份',
    month        int                                null comment '月份',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted      tinyint  default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '节气信息表' charset = utf8mb4;

create index idx_term_date
    on yw_solar_term (term_date);

create index idx_year_month
    on yw_solar_term (year, month);

create table yw_user
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    uid             varchar(32)                              not null comment '业务主键',
    openid          varchar(100)                             null comment '微信OpenID',
    unionid         varchar(100)                             null comment '微信UnionID',
    nickname        varchar(50)                              null comment '昵称',
    avatar          varchar(255)                             null comment '头像URL',
    phone           varchar(20)                              null comment '手机号',
    gender          tinyint        default 0                 null comment '性别：0未知 1男 2女',
    birthday        date                                     null comment '生日',
    level           int            default 0                 null comment '会员等级：0普通 1银牌 2金牌 3钻石',
    points          int            default 0                 null comment '积分',
    balance         decimal(10, 2) default 0.00              null comment '账户余额',
    total_amount    decimal(12, 2) default 0.00              null comment '累计消费金额',
    order_count     int            default 0                 null comment '订单数量',
    status          tinyint        default 1                 null comment '状态：0禁用 1启用',
    register_time   datetime                                 null comment '注册时间',
    last_login_time datetime                                 null comment '最后登录时间',
    create_time     datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by       varchar(32)                              null comment '创建人',
    update_by       varchar(32)                              null comment '更新人',
    deleted         tinyint        default 0                 null comment '逻辑删除',
    constraint uk_openid
        unique (openid),
    constraint uk_uid
        unique (uid)
)
    comment '用户表' charset = utf8mb4;

create index idx_level
    on yw_user (level);

create index idx_phone
    on yw_user (phone);

create index idx_status
    on yw_user (status);

create table yw_user_address
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    uid            varchar(32)                        not null comment '业务主键',
    user_uid       varchar(32)                        not null comment '用户UID',
    receiver_name  varchar(50)                        not null comment '收货人姓名',
    receiver_phone varchar(20)                        not null comment '收货人电话',
    province       varchar(50)                        null comment '省份',
    city           varchar(50)                        null comment '城市',
    district       varchar(50)                        null comment '区县',
    detail_address varchar(200)                       not null comment '详细地址',
    postal_code    varchar(10)                        null comment '邮政编码',
    is_default     tinyint  default 0                 null comment '是否默认：0否 1是',
    tag            varchar(20)                        null comment '标签：家、公司、学校等',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by      varchar(32)                        null comment '创建人',
    update_by      varchar(32)                        null comment '更新人',
    deleted        tinyint  default 0                 null comment '逻辑删除',
    constraint uk_uid
        unique (uid)
)
    comment '收货地址表' charset = utf8mb4;

create index idx_is_default
    on yw_user_address (is_default);

create index idx_user_uid
    on yw_user_address (user_uid);

create table yw_user_favorite
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    uid         varchar(32)                        not null comment '业务主键',
    user_uid    varchar(32)                        not null comment '用户UID',
    goods_uid   varchar(32)                        not null comment '商品UID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 null comment '逻辑删除：0未删除 1已删除',
    constraint uk_uid
        unique (uid),
    constraint uk_user_goods
        unique (user_uid, goods_uid) comment '用户对同一商品只能收藏一次'
)
    comment '用户收藏表' charset = utf8mb4;

create index idx_create_time
    on yw_user_favorite (create_time);

create index idx_goods_uid
    on yw_user_favorite (goods_uid);

create index idx_user_uid
    on yw_user_favorite (user_uid);

