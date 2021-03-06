package com.dyl.gw.support.task;

import com.diyiliu.plugin.task.ITask;
import com.diyiliu.plugin.util.GpsCorrectUtil;
import com.diyiliu.plugin.util.JacksonUtil;
import com.dyl.gw.support.jpa.dto.PetGps;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.dto.PetGpsHis;
import com.dyl.gw.support.jpa.facade.PetGpsCurJpa;
import com.dyl.gw.support.jpa.facade.PetGpsHisJpa;
import com.dyl.gw.support.model.BtsInfo;
import com.dyl.gw.support.model.Position;
import com.dyl.gw.support.model.WifiInfo;
import com.dyl.gw.support.util.GdLocationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description: LocationTask
 * 基站 & WIFI定位
 * <p>
 * Author: DIYILIU
 * Update: 2018-07-13 09:22
 */

@Slf4j
@Component
public class LocationTask implements ITask {
    private final static Queue<PetGps> gpsPool = new ConcurrentLinkedQueue();

    @Resource
    private PetGpsCurJpa petGpsCurJpa;

    @Resource
    private PetGpsHisJpa petGpsHisJpa;

    @Resource
    private GdLocationUtil locationUtil;

    @Scheduled(fixedDelay = 1000, initialDelay = 10 * 1000)
    @Override
    public void execute() {
        while (!gpsPool.isEmpty()) {
            PetGps petGps = gpsPool.poll();
            String petKey = petGps.getPetKey();
            String[] keys = petKey.split(",");
            String device = keys[1];

            int location = petGps.getLocation();
            // GPS 定位
            if (location == 1) {
                updateGps(petGps);
                log.info("更新设备[{}]位置, GPS有效定位。", device);

                return;
            }

            Position position = null;
            List<BtsInfo> btsInfoList = petGps.getBtsInfoList();
            List<WifiInfo> wifiInfoList = petGps.getWifiInfoList();

            // 优先wifi定位
            if (CollectionUtils.isNotEmpty(wifiInfoList)) {
                Position wfPosition = locationUtil.wifiLocation(device, wifiInfoList);

                if (wfPosition != null && wfPosition.getType() > 0) {
                    // wifi定位
                    position = wfPosition;
                }
            }

            if (CollectionUtils.isNotEmpty(btsInfoList)) {
                Position btsPosition = locationUtil.btsLocation(device, btsInfoList);

                if (btsPosition != null && btsPosition.getType() > 0) {
                    if (position == null || btsPosition.getRadius() < position.getRadius()) {
                        // 采用基站定位
                        position = btsPosition;
                    }
                }
            }

            String info = "";
            // 高德有效定位
            if (position != null && position.getType() > 0) {
                String[] lngLat = position.getLocation().split(",");

                // GCJ_02 -> GPS_84 火星坐标系转原始坐标系
                double[] latLng = GpsCorrectUtil.gcj02_To_Gps84(Double.valueOf(lngLat[1]), Double.valueOf(lngLat[0]));
                double lat = latLng[0];
                double lng = latLng[1];

                petGps.setWgs84Lat(lat);
                petGps.setWgs84Lng(lng);
                petGps.setLocation(position.getMode());
                petGps.setAddress(position.getAddress());

                info = "地址: " + petGps.getAddress() + ", 半径:  " + position.getRadius() + ", 定位方式: " + position.getMode();
            }

            updateGps(petGps);
            log.info("更新设备[{}]位置, {}。", device, StringUtils.isEmpty(info) ? "无效定位" : info);
        }
    }

    public static void dealUD(PetGps petGps) {
        gpsPool.add(petGps);
    }

    private void updateGps(PetGps petGps) {
        String petKey = petGps.getPetKey();
        String[] keys = petKey.split(",");

        long deviceId = Long.valueOf(keys[0]);
        String device = keys[1];

        // 经纬度坐标系转换
        initLatLng(petGps);

        // 目的为了 父类 转 子类
        String gpsJson = JacksonUtil.toJson(petGps);
        try {
            // 1、更新当前位置信息
            PetGpsCur gpsCur = JacksonUtil.toObject(gpsJson, PetGpsCur.class);
            gpsCur.setId(deviceId);
            gpsCur.setDevice(device);
            gpsCur.setStatus(1);

            petGpsCurJpa.save(gpsCur);

            // 2、插入轨迹数据
            PetGpsHis gpsHis = JacksonUtil.toObject(gpsJson, PetGpsHis.class);
            gpsHis.setDeviceId(deviceId);
            gpsHis.setBtsData(JacksonUtil.toJson(petGps.getBtsInfoList()));
            gpsHis.setWifiData(JacksonUtil.toJson(petGps.getWifiInfoList()));

            petGpsHisJpa.save(gpsHis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLatLng(PetGps petGps) {
        double lat = petGps.getWgs84Lat();
        double lng = petGps.getWgs84Lng();

        double[] latLng = GpsCorrectUtil.gps84_To_Gcj02(lat, lng);
        petGps.setGcj02Lat(latLng[0]);
        petGps.setGcj02Lng(latLng[1]);

        latLng = GpsCorrectUtil.gps84_To_bd09(lat, lng);
        petGps.setBd09Lat(latLng[0]);
        petGps.setBd09Lng(latLng[1]);
    }
}
