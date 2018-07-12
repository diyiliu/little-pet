package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: PetGps
 * Author: DIYILIU
 * Update: 2018-07-12 10:02
 */

@Data
@Entity
@Table(name = "pet_gps")
public class PetGps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private Date systemTime;

    private Date gpsTime;

    @Column(name = "wgs84_lat")
    private Double wgs84Lat;

    @Column(name = "wgs84_lng")
    private Double wgs84Lng;

    @Column(name = "gcj02_lat")
    private Double gcj02Lat;

    @Column(name = "gcj02_lng")
    private Double gcj02Lng;

    @Column(name = "bd09_lat")
    private Double bd09Lat;

    @Column(name = "bd09_lng")
    private Double bd09Lng;

    /** 定位状态(0:无效定位,1:GPS定位, 2:基站定位, 3:WIFI定位)*/
    private Integer location;

    private String address;

    private Integer step;

    private Double speed;

    private Double direction;

    private Double altitude;

    private Integer satellite;

    private Integer signal;

    /** 百分比*/
    private Integer voltage;

    /** 设备在线状态*/
    private Integer status;
}
