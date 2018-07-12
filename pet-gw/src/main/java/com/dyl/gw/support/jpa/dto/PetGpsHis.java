package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * Description: PetGpsHis
 * Author: DIYILIU
 * Update: 2018-07-12 22:17
 */

@Data
@Entity
@Table(name = "pet_trace")
public class PetGpsHis extends PetGps{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long petId;

    private String btsData;

    private String wifiData;
}
