package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * Description: PetGpsHis
 * Author: DIYILIU
 * Update: 2018-07-12 22:17
 */

@Entity
@Table(name = "pet_trace")
public class PetGpsHis extends PetGps{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;

    private String btsData;

    private String wifiData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getBtsData() {
        return btsData;
    }

    public void setBtsData(String btsData) {
        this.btsData = btsData;
    }

    public String getWifiData() {
        return wifiData;
    }

    public void setWifiData(String wifiData) {
        this.wifiData = wifiData;
    }
}
