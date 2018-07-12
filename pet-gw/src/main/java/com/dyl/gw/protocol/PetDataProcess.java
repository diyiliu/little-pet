package com.dyl.gw.protocol;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.model.SendMsg;
import com.diyiliu.plugin.util.DateUtil;
import com.diyiliu.plugin.util.GpsCorrectUtil;
import com.dyl.gw.support.jpa.dto.PetGps;
import com.dyl.gw.support.jpa.facade.PetGpsJpa;
import com.dyl.gw.support.model.BtsInfo;
import com.dyl.gw.support.model.MsgBody;
import com.dyl.gw.support.model.Position;
import com.dyl.gw.support.model.WifiInfo;
import com.dyl.gw.support.task.MsgSenderTask;
import com.dyl.gw.support.util.GdLocationUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description: PetDataProcess
 * Author: DIYILIU
 * Update: 2018-07-11 22:46
 */

@Slf4j
@Component
public class PetDataProcess {

    @Resource
    private ICache onlineCacheProvider;

    @Resource
    private PetGpsJpa petGpsJpa;

    @Resource
    private GdLocationUtil locationUtil;

    public void parse(MsgBody msgBody, ChannelHandlerContext ctx) {
        String cmd = msgBody.getCmd();
        String factory = msgBody.getFactory();
        String device = msgBody.getDevice();
        int serial = msgBody.getSerial();

        // 消息内容
        String content = msgBody.getContent();
        String[] msgArray = content.split(",");

        // 保持在线
        onlineCacheProvider.put(device, new MsgPipeline(ctx, System.currentTimeMillis()));
        log.info("上行, 设备[{}], 指令[{}],  内容: {}", device, cmd, msgBody.getText());

        // 初始化
        if ("INIT".equals(cmd)) {
            String resp = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0006*INIT,1]";
            respCmd(device, cmd, serial, resp.getBytes());

            return;
        }

        PetGps petGps = petGpsJpa.findByDeviceId(device);
        // 心跳
        if ("LK".equals(cmd)) {
            String resp = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0016*LK," + DateUtil.dateToString(new Date()).replace(" ", ",") + "]";
            respCmd(device, cmd, serial, resp.getBytes());

            int voltage = Integer.valueOf(msgArray[msgArray.length - 1]);
            if (msgArray.length > 2) {
                int step = Integer.valueOf(msgArray[1]);
                petGps.setStep(step);
            }
            petGps.setVoltage(voltage);
            petGps.setSystemTime(new Date());
            petGps.setStatus(1);
            petGpsJpa.save(petGps);

            return;
        }

        // 位置
        if ("UD".equals(cmd)) {
            String resp = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0002*UD]";
            respCmd(device, cmd, serial, resp.getBytes());

            String dmy = msgArray[1];
            String hms = msgArray[2];

            Date gpsTime = toDate(dmy, hms);

            int location = 0;
            double lat = 0;
            double lng = 0;
            String loc = msgArray[3];
            if ("A".equals(loc)) {
                location = 1;

                lat = Double.valueOf(msgArray[4]);
                if ("S".equals(msgArray[5])) {
                    lat = -1 * lat;
                }

                lng = Double.valueOf(msgArray[6]);
                if ("W".equals(msgArray[7])) {
                    lng = -1 * lng;
                }
            }

            double speed = Double.valueOf(msgArray[8]);
            double direction = Double.valueOf(msgArray[9]);
            double altitude = Double.valueOf(msgArray[10]);
            int satellite = Integer.valueOf(msgArray[11]);
            int signal = Integer.valueOf(msgArray[12]);
            int voltage = Integer.valueOf(msgArray[13]);

            petGps.setSpeed(speed);
            petGps.setDirection(direction);
            petGps.setAltitude(altitude);
            petGps.setSatellite(satellite);
            petGps.setSignal(signal);
            petGps.setVoltage(voltage);

            petGps.setGpsTime(gpsTime);

            String terminalStatus = msgArray[16];
            Position position = null;
            try {
                int btsCount = Integer.valueOf(msgArray[17]);
                int from = 19;
                int to = 19 + 2 + 3 * btsCount;
                String[] btsArray = Arrays.copyOfRange(msgArray, from, to);

                List<BtsInfo> btsInfoList = new ArrayList();
                if (btsArray.length > 0) {
                    int mcc = Integer.valueOf(btsArray[0]);
                    int mnc = Integer.valueOf(btsArray[1]);

                    for (int i = 0; i < btsCount; i++) {
                        int lac = Integer.valueOf(btsArray[2 + i * 3]);
                        long cellid = Long.valueOf(btsArray[2 + i * 3 + 1]);
                        int sig = Integer.valueOf(btsArray[2 + i * 3 + 2]);

                        BtsInfo btsInfo = new BtsInfo();
                        btsInfo.setMcc(mcc);
                        btsInfo.setMnc(mnc);
                        btsInfo.setLac(lac);
                        btsInfo.setCellid(cellid);
                        btsInfo.setSignal(sig);

                        btsInfoList.add(btsInfo);
                    }

                    // 基站定位
                    position = locationUtil.btsLocation(device, btsInfoList);
                }

                if (msgArray.length > to) {
                    int wifiCount = Integer.valueOf(msgArray[to]);

                    from = to + 1;
                    to = from + 3 * wifiCount;
                    String[] wifiArray = Arrays.copyOfRange(msgArray, from, to);

                    List<WifiInfo> wifiInfoList = new ArrayList();
                    if (wifiArray.length > 0) {
                        for (int i = 0; i < wifiCount; i++) {
                            String ssid = wifiArray[i * 3];
                            String mac = wifiArray[i * 3 + 1];
                            int sig = Integer.valueOf(wifiArray[i * 3 + 2]);

                            WifiInfo wifiInfo = new WifiInfo();
                            wifiInfo.setMac(mac);
                            wifiInfo.setSignal(sig);
                            wifiInfo.setSsid(ssid);

                            wifiInfoList.add(wifiInfo);
                        }

                        Position wifiPosition = locationUtil.wifiLocation(device, wifiInfoList);
                        if (position == null) {
                            // 基站定位
                            position = wifiPosition;
                        } else {
                            if (wifiPosition != null && wifiPosition.getRadius() < position.getRadius()) {
                                // 基站定位
                                position = wifiPosition;
                            }
                        }
                    }
                }

                // 定位方式
                if (location == 0 && position != null) {
                    if (position.getType() > 0) {
                        String[] lngLat = position.getLocation().split(",");

                        lng = Double.valueOf(lngLat[0]);
                        lat = Double.valueOf(lngLat[1]);
                        location = position.getMode();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            petGps.setLocation(location);
            // 有效定位
            if (location > 0){
                petGps.setWgs84Lat(lat);
                petGps.setWgs84Lng(lng);

                double[] latLng = GpsCorrectUtil.gps84_To_Gcj02(lat, lng);
                petGps.setGcj02Lat(latLng[0]);
                petGps.setGcj02Lng(latLng[1]);

                latLng = GpsCorrectUtil.gps84_To_bd09(lat, lng);
                petGps.setBd09Lat(latLng[0]);
                petGps.setBd09Lng(latLng[1]);

                if (position != null){
                    petGps.setAddress(position.getAddress());
                }
            }
            petGps.setSystemTime(new Date());
            petGps.setStatus(1);
            petGpsJpa.save(petGps);

            return;
        }

        log.warn("未知: {}", msgBody.toString());
    }

    private Date toDate(String dmy, String hms) {
        int day = Integer.valueOf(dmy.substring(0, 2));
        int month = Integer.valueOf(dmy.substring(2, 4));
        int year = 2000 + Integer.valueOf(dmy.substring(4));

        int hour = Integer.valueOf(hms.substring(0, 2));
        int minute = Integer.valueOf(hms.substring(2, 4));
        int second = Integer.valueOf(hms.substring(4));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    private void respCmd(String device, String cmd, int serial, byte[] content) {
        SendMsg msg = new SendMsg();
        msg.setDevice(device);
        msg.setCmd(cmd);
        msg.setSerial(serial);
        msg.setContent(content);

        MsgSenderTask.send(msg);
    }
}
