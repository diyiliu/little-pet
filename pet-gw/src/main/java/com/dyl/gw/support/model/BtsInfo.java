package com.dyl.gw.support.model;

import lombok.Data;
/**
 * Description: BtsInfo
 * 基站信息
 *
 * Author: DIYILIU
 * Update: 2018-07-12 10:37
 */

@Data
public class BtsInfo {

    /** 国家代码 */
    private Integer mcc;

    /** 网号 */
    private Integer mnc;

    /** 位置区域码 */
    private Integer lac;

    /** 基站小区编号 */
    private Long cellid;

    /** 信号强度(0到-113dbm, 如获得信号强度为正数，则请按照以下公式进行转
     换：获得的正信号强度 * 2 – 113) */
    private Integer signal;

    @Override
    public String toString() {
        return mcc + "," + mnc + "," + lac + "," + cellid + "," + signal;
    }
}
