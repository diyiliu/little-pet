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

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: LocationTask
 * 基站 & WIFI定位
 * <p>
 * Author: DIYILIU
 * Update: 2018-07-13 09:22
 */

@Slf4j
public class LocationTask implements ITask, Runnable {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final static Queue<PetGps> gpsPool = new ConcurrentLinkedQueue();

    @Resource
    private PetGpsCurJpa petGpsCurJpa;

    @Resource
    private PetGpsHisJpa petGpsHisJpa;

    @Resource
    private GdLocationUtil locationUtil;

    @Override
    public void execute() {
        executorService.scheduleWithFixedDelay(this, 1, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        while (!gpsPool.isEmpty()) {
            PetGps petGps = gpsPool.poll();
            String petKey = petGps.getPetKey();
            String[] keys = petKey.split(",");
            String device = keys[1];

            int location = petGps.getLocation();
            // GPS 定位
            if (location == 1) {
                updateGps(petGps);
                log.info("更新设备[{}]当前位置, GPS有效定位 ...", device);

                return;
            }

            Position position = null;
            List<BtsInfo> btsInfoList = petGps.getBtsInfoList();
            List<WifiInfo> wifiInfoList = petGps.getWifiInfoList();
            try {
                if (CollectionUtils.isNotEmpty(btsInfoList)) {
                    position = locationUtil.btsLocation(device, btsInfoList);
                }

                if (CollectionUtils.isNotEmpty(wifiInfoList)) {
                    Position wfPosition = locationUtil.wifiLocation(device, wifiInfoList);

                    if (position == null) {
                        // 基站定位
                        position = wfPosition;
                    } else {
                        if (wfPosition != null && wfPosition.getRadius() < position.getRadius()) {
                            // 基站定位
                            position = wfPosition;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("高德定位异常: {}!", e.getMessage());
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

                info = "地址: " + petGps.getAddress() + ", 半径:  " + position.getRadius();
            }

            updateGps(petGps);
            log.info("更新设备[{}]当前位置, {}。", device, info);
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
            gpsCur.setDeviceId(device);
            gpsCur.setStatus(1);

            petGpsCurJpa.save(gpsCur);

            // 2、插入轨迹数据
            PetGpsHis gpsHis = JacksonUtil.toObject(gpsJson, PetGpsHis.class);
            gpsHis.setPetId(deviceId);
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
