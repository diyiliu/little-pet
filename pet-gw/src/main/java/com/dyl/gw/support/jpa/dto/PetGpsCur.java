package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * Description: PetGpsCur
 * Author: DIYILIU
 * Update: 2018-07-12 22:16
 */

@Entity
@Table(name = "pet_gps")
public class PetGpsCur extends PetGps{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    /** 设备在线状态 0:离线, 1:在线*/
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
