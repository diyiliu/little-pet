package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * Description: PetGpsCur
 * Author: DIYILIU
 * Update: 2018-07-12 22:16
 */

@Data
@Entity
@Table(name = "pet_gps")
public class PetGpsCur extends PetGps{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    /** 设备在线状态 0:离线, 1:在线*/
    private Integer status;
}
