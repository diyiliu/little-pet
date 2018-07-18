drop table if exists init_data;

drop table if exists pet_gps;

drop table if exists pet_info;

drop table if exists pet_trace;

drop table if exists raw_data;

drop table if exists user_info;

/*==============================================================*/
/* Table: init_data                                             */
/*==============================================================*/
create table init_data
(
   id                   int not null auto_increment,
   device               varchar(50) comment '设备识别码',
   datetime             datetime comment '时间',
   step                 int comment '步数',
   primary key (id)
);

alter table init_data comment '初始化数据保存';

/*==============================================================*/
/* Table: pet_gps                                               */
/*==============================================================*/
create table pet_gps
(
   id                   int not null auto_increment,
   device               int comment '设备ID',
   system_time          datetime comment '系统时间',
   gps_time             datetime comment 'GPS时间',
   wgs84_lat            numeric(12,8) comment '原始纬度',
   wgs84_lng            numeric(12,8) comment '原始经度',
   gcj02_lat            numeric(12,8) comment '高德纬度',
   gcj02_lng            numeric(12,8) comment '高德经度',
   bd09_lat             numeric(12,8) comment '百度纬度',
   bd09_lng             numeric(12,8) comment '百度经度',
   location             int comment '定位状态(0:无效定位,1:GPS定位,2:WIFI定位,3:基站定位)',
   step                 int comment '步数',
   speed                numeric(5,2) comment '速度',
   direction            int comment '方向',
   altitude             numeric(5,2) comment '海拔',
   signal               int comment '信号强度',
   satellite            int comment '卫星数',
   voltage              numeric(5,2) comment '电压(%)',
   status               int comment '设备状态',
   primary key (id)
);

alter table pet_gps comment '宠物位置';

/*==============================================================*/
/* Table: pet_info                                              */
/*==============================================================*/
create table pet_info
(
   id                   int not null auto_increment,
   device_id            int comment '设备ID',
   user_id              int comment '用户ID',
   name                 varchar(50) comment '名称',
   sex                  varchar(10) comment '性别',
   breed                varchar(100) comment '品种',
   birthday             datetime comment '出生时间',
   breed_date           date comment '喂养时间',
   primary key (id)
);

alter table pet_info comment '宠物信息';

/*==============================================================*/
/* Table: pet_trace                                             */
/*==============================================================*/
create table pet_trace
(
   id                   int not null auto_increment,
   device_id            int comment '设备ID',
   system_time          datetime comment '系统时间',
   gps_time             datetime comment 'GPS时间',
   wgs84_lat            numeric(12,8) comment '原始纬度',
   wgs84_lng            numeric(12,8) comment '原始经度',
   gcj02_lat            numeric(12,8) comment '高德纬度',
   gcj02_lng            numeric(12,8) comment '高德经度',
   bd09_lat             numeric(12,8) comment '百度纬度',
   bd09_lng             numeric(12,8) comment '百度经度',
   bts_data             text comment '基站数据',
   wifi_data            text comment 'WIFI数据',
   location             int comment '定位状态(0:无效定位,1:GPS定位,2:WIFI定位,3:基站定位)',
   step                 int comment '步数',
   speed                numeric(5,2) comment '速度',
   direction            int comment '方向',
   altitude             numeric(5,2) comment '海拔',
   signal               int comment '信号强度',
   satellite            int comment '卫星数',
   voltage              numeric(5,2) comment '电压(%)',
   primary key (id)
);

alter table pet_trace comment '宠物轨迹';

/*==============================================================*/
/* Table: raw_data                                              */
/*==============================================================*/
create table raw_data
(
   id                   int not null auto_increment,
   device               varchar(50) comment '设备识别码',
   cmd                  varchar(20) comment '指令ID',
   data                 text comment '内容',
   datetime             datetime comment '时间',
   primary key (id)
);

alter table raw_data comment '原始指令';

/*==============================================================*/
/* Table: user_info                                             */
/*==============================================================*/
create table user_info
(
   id                   int not null auto_increment,
   username             varchar(50) comment '用户名',
   password             varchar(100) comment '密码',
   name                 varchar(30) comment '姓名',
   wechat               varchar(50) comment '微信号',
   tel                  varchar(20) comment '手机号',
   email                varchar(80) comment '邮箱',
   address              varchar(200) comment '住址',
   primary key (id)
);

alter table user_info comment '用户信息';
