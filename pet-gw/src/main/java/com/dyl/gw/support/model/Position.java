package com.dyl.gw.support.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Description: Position
 * Author: DIYILIU
 * Update: 2018-07-12 11:37
 */

@Data
public class Position {

    /** 0: 无结果, 其他正常*/
    private Integer type;

    private String province;

    private String city;

    @JsonProperty("desc")
    private String address;

    // 经纬度 (117.2649875,34.2873991)
    private String location;

    /** 定位半径 */
    private Integer radius;

    // 2:基站, 3:WIFI
    @JsonIgnore
    private Integer mode;
}
