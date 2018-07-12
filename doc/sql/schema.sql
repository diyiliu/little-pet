drop table if exists pet_gps;

drop table if exists pet_trace;

drop table if exists raw_data;

/*==============================================================*/
/* Table: pet_gps                                               */
/*==============================================================*/
create table pet_gps
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
   location             int comment '定位状态(0:无效定位,1:GPS定位, 2:基站定位, 3:WIFI定位)',
   step                 int comment '步数',
   speed                numeric(5,2) comment '速度',
   direction            int comment '方向',
   altitude             numeric(5,2) comment '海拔',
   satellite            int comment '卫星数',
   voltage              numeric(5,2) comment '电压(%)',
   status               int comment '设备状态',
   primary key (id)
);

alter table pet_gps comment '宠物位置';

/*==============================================================*/
/* Table: pet_trace                                             */
/*==============================================================*/
create table pet_trace
(
   id                   int not null auto_increment,
   pet_Id               int comment '宠物ID',
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
   imei                 varchar(50) comment '设备识别码',
   cmd                  varchar(5) comment '指令ID',
   data                 text comment '内容',
   datetime             datetime comment '时间',
   primary key (id)
);

alter table raw_data comment '原始指令';
