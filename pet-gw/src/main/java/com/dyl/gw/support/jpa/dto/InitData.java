package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: InitData
 * Author: DIYILIU
 * Update: 2018-07-16 09:25
 */

@Data
@Entity
@Table(name = "init_data")
public class InitData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deviceId;

    private Integer step;

    private Date datetime;
}
