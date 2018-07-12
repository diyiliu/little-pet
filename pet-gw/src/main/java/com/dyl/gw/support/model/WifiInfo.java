package com.dyl.gw.support.model;

import lombok.Data;

/**
 * Description: WifiInfo
 * Author: DIYILIU
 * Update: 2018-07-12 10:53
 */

@Data
public class WifiInfo {

    /** MAC地址 */
    private String mac;

    /** 信号强度 */
    private Integer signal;

    /** 名称 */
    private String ssid;

    @Override
    public String toString() {
        return mac + "," + signal + "," + ssid;
    }
}
