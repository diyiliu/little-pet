package com.dyl.gw.support.util;

import com.diyiliu.plugin.util.JacksonUtil;
import com.dyl.gw.support.model.BtsInfo;
import com.dyl.gw.support.model.GdLocation;
import com.dyl.gw.support.model.Position;
import com.dyl.gw.support.model.WifiInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: GdLocationUtil
 * Author: DIYILIU
 * Update: 2018-07-12 10:27
 */

public class GdLocationUtil {

    @Resource
    private RestTemplate restTemplate;

    private String amapUrl;

    private String userKey;

    public Position btsLocation(String imei, List<BtsInfo> btsInfoList) throws Exception {
        String bts = btsInfoList.get(0).toString();

        String nearBts = "";
        if (btsInfoList.size() > 1) {
            for (int i = 1; i < btsInfoList.size(); i++) {
                BtsInfo btsInfo = btsInfoList.get(i);
                nearBts += "|" + btsInfo.toString();
            }
        }

        StringBuffer strBuf = new StringBuffer("?");
        strBuf.append("accesstype=0").
        append("&cmda=0").
        append("&output=json").
        append("&key=").append(userKey).
        append("&imei=").append(imei).
        append("&bts=").append(bts);
        if (StringUtils.isNotEmpty(nearBts)) {
            strBuf.append("&nearbts=").append(nearBts.substring(1));
        }
        amapUrl += strBuf.toString();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(amapUrl, String.class);
        GdLocation location = JacksonUtil.toObject(responseEntity.getBody(), GdLocation.class);
        if (location.getStatus() == 1) {
            Position position = location.getResult();
            position.setMode(2);

            return position;
        }

        return null;
    }

    public Position wifiLocation(String imei, List<WifiInfo> wifiInfoList) throws Exception {
        String macs = "";
        for (int i = 0; i < wifiInfoList.size(); i++) {
            WifiInfo wifiInfo = wifiInfoList.get(i);
            macs += "|" + wifiInfo.toString();
        }

        StringBuffer strBuf = new StringBuffer("?");
        strBuf.append("accesstype=1").
                append("&output=json").
                append("&key=").append(userKey).
                append("&imei=").append(imei).
                append("&macs=").append(macs.substring(1));
        amapUrl += strBuf.toString();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(amapUrl, String.class);
        GdLocation location = JacksonUtil.toObject(responseEntity.getBody(), GdLocation.class);
        if (location.getStatus() == 1) {
            Position position = location.getResult();
            position.setMode(3);

            return position;
        }

        return null;
    }

    public void setAmapUrl(String amapUrl) {
        this.amapUrl = amapUrl;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
