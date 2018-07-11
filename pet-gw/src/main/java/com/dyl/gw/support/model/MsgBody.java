package com.dyl.gw.support.model;

import lombok.Data;

/**
 * Description: MsgBody
 * Author: DIYILIU
 * Update: 2018-07-11 22:47
 */

@Data
public class MsgBody {

    private String factory;

    private String device;

    private Integer serial;

    private Integer length;

    private String cmd;

    private String content;

    private String text;
}
