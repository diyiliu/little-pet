package com.dyl.gw.support.util;

import com.diyiliu.plugin.util.JacksonUtil;
import com.dyl.gw.support.model.BtsInfo;
import com.dyl.gw.support.model.GdLocation;
import com.dyl.gw.support.model.Position;
import com.dyl.gw.support.model.WifiInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Description: GdLocationUtil
 * Author: DIYILIU
 * Update: 2018-07-12 10:27
 */

@Slf4j
public class GdLocationUtil {

    @Resource
    private RestTemplate restTemplate;

    private String amapUrl;

    private String userKey;


    /**
     * 基站定位
     *
     * @param imei
     * @param btsInfoList (取前三个基站,太多414 错误)
     * @return
     * @throws Exception
     */
    public Position btsLocation(String imei, List<BtsInfo> btsInfoList) {
        String bts = btsInfoList.get(0).toString();
        // int length = btsInfoList.size() > 3 ? 3 : btsInfoList.size();

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

        String url = amapUrl + strBuf.toString();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            log.info("基站定位[{}], 结果:{}", url, responseEntity.getBody());

            GdLocation location = JacksonUtil.toObject(responseEntity.getBody(), GdLocation.class);
            if (location.getStatus() == 1) {
                Position position = location.getResult();
                position.setMode(2);

                return position;
            } else {
                log.warn("基站定位异常: {}", url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("基站定位异常: {}!", url);
        }

        return null;
    }

    public Position wifiLocation(String imei, List<WifiInfo> wifiInfoList) {
        // int length = wifiInfoList.size() > 3 ? 3 : wifiInfoList.size();

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

        String url = amapUrl + strBuf.toString();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            log.info("WIFI定位[{}], 结果:{}", url, responseEntity.getBody());

            GdLocation location = JacksonUtil.toObject(responseEntity.getBody(), GdLocation.class);
            if (location.getStatus() == 1) {
                Position position = location.getResult();
                position.setMode(3);

                return position;
            } else {
                log.warn("WIFI定位异常: {}", url);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WIFI定位异常: {}!", url);
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
