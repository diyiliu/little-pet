package com.dyl.gw.support.util;

import com.diyiliu.plugin.util.JacksonUtil;
import com.dyl.gw.support.model.BtsInfo;
import com.dyl.gw.support.model.GdLocation;
import com.dyl.gw.support.model.Position;
import com.dyl.gw.support.model.WifiInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map paramMap = new HashMap();
        paramMap.put("accesstype", 0);
        paramMap.put("cmda", 0);
        paramMap.put("output", "json");
        paramMap.put("key", "5577e6cf339dbe2376de0369149210bf");
        paramMap.put("imei", imei);
        paramMap.put("bts", bts);
        if (StringUtils.isNotEmpty(nearBts)) {
            paramMap.put("nearbts", nearBts.substring(1));
        }

        amapUrl += "?accesstype=0&imei=589468010000246&key=5577e6cf339dbe2376de0369149210bf&cmda=0&bts=460,0,21238,10920,57&nearbts=460,0,21238,10920,57|460,0,21238,13006,49|460,0,21238,10921,41|460,0,21238,19976,39|460,0,21238,8907,38&output=json";

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

        Map paramMap = new HashMap();
        paramMap.put("accesstype", 1);
        paramMap.put("output", "json");
        paramMap.put("key", userKey);
        paramMap.put("imei", imei);
        paramMap.put("macs", macs.substring(1));

        HttpEntity<String> requestEntity = new HttpEntity(paramMap);
        ResponseEntity<String> responseEntity = restTemplate.exchange(amapUrl, HttpMethod.GET, requestEntity, String.class);
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
