package com.dyl.gw.support.model;

import lombok.Data;

/**
 * Description: GdLocation
 * 高德定位
 *
 * Author: DIYILIU
 * Update: 2018-07-12 10:25
 */

@Data
public class GdLocation {

    /** 0:s失败,1:成功*/
    private Integer status;

    private String info;

    private Position result;
}
