package com.dyl.gw.support.jpa.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: PetInfo
 * Author: DIYILIU
 * Update: 2018-07-18 15:18
 */

@Data
@Entity
@Table(name = "pet_info")
public class PetInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "device_id")
    private PetGpsCur petGpsCur;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String sex;

    // 品种
    private String breed;

    // 喂养时间
    private Date breedDate;

    // 出生时间
    private Date birthday;
}
