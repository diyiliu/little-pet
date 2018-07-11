package com.dyl.gw.support.jpa.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: RawData
 * Author: DIYILIU
 * Update: 2018-07-11 22:21
 */

@Data
@Entity
@Table(name = "raw_data")
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String device;

    private String cmd;

    private String data;

    private String flow;

    private Date datetime;
}
