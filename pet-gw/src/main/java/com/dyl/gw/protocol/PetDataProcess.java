package com.dyl.gw.protocol;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.model.SendMsg;
import com.diyiliu.plugin.util.DateUtil;
import com.dyl.gw.support.jpa.dto.InitData;
import com.dyl.gw.support.jpa.dto.PetGps;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.facade.InitDataJpa;
import com.dyl.gw.support.jpa.facade.PetGpsCurJpa;
import com.dyl.gw.support.model.BtsInfo;
import com.dyl.gw.support.model.MsgBody;
import com.dyl.gw.support.model.WifiInfo;
import com.dyl.gw.support.task.LocationTask;
import com.dyl.gw.support.task.MsgSenderTask;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private PetGpsCurJpa petGpsCurJpa;

    @Resource
    private InitDataJpa initDataJpa;


    private List<String> ackCmds = new ArrayList();

    public PetDataProcess() {

        // 上报间隔
        ackCmds.add("UPLOAD");
        // 实时定位
        ackCmds.add("CR");
        // 关机
        ackCmds.add("POWEROFF");
        // 重启
        ackCmds.add("RESET");
        // 寻找设备
        ackCmds.add("FIND");
    }

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
        log.debug("上行, 设备[{}], 指令[{}],  内容: {}", device, cmd, msgBody.getText());

        PetGpsCur curGps = petGpsCurJpa.findByDevice(device);
        long petId = curGps.getId();

        // 初始化
        if ("INIT".equals(cmd)) {
            String resp = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0006*INIT,1]";

            // 先保存 INIT 之前得数据
            toInitData(curGps);

            respCmd(device, cmd, serial, resp.getBytes());

            return;
        }

        // 心跳
        if ("LK".equals(cmd)) {
            Calendar calendar = Calendar.getInstance();
            Date utc = reviseTime(calendar, -1);

            String resp = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0016*LK," + DateUtil.dateToString(utc).replace(" ", ",") + "]";
            respCmd(device, cmd, serial, resp.getBytes());

            int voltage = Integer.valueOf(msgArray[msgArray.length - 1]);
            if (msgArray.length > 2) {
                int step = Integer.valueOf(msgArray[1]);

                Date now = new Date();
                if (step == 0 && inSameDay(curGps.getSystemTime(), now)){

                    return;
                }

                // 同一天内 记步累加
                int initStep = initStep(curGps.getId());
                step += initStep;

                // 记步数据异常
                if (step < curGps.getStep() && inSameDay(curGps.getSystemTime(), now)){
                    toInitData(curGps);

                    step += curGps.getStep();
                }

                curGps.setStep(step);
            }
            curGps.setVoltage(voltage);
            curGps.setSystemTime(new Date());
            curGps.setStatus(1);
            petGpsCurJpa.save(curGps);

            return;
        }

        // 位置
        if ("UD".equals(cmd)) {
            String resp = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0002*UD]";
            respCmd(device, cmd, serial, resp.getBytes());

            String dmy = msgArray[1];
            String hms = msgArray[2];
            Date date = toDate(dmy, hms);
            // 修正时差偏移
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Date gpsTime = reviseTime(calendar, 1);

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

            PetGps petGps = new PetGps();
            petGps.setLocation(location);
            petGps.setWgs84Lat(lat);
            petGps.setWgs84Lng(lng);
            petGps.setSpeed(speed);
            petGps.setDirection(direction);
            petGps.setAltitude(altitude);
            petGps.setSatellite(satellite);
            petGps.setSignal(signal);
            petGps.setVoltage(voltage);

            // 查询当前步数
            petGps.setStep(curGps.getStep());
            petGps.setGpsTime(gpsTime);

            //String terminalStatus = msgArray[16];
            int btsCount = Integer.valueOf(msgArray[17]);
            int from = 19;
            int to = 19 + 2 + 3 * btsCount;
            String[] btsArray = Arrays.copyOfRange(msgArray, from, to);

            // 基站信息
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
            }

            // wifi 信息
            List<WifiInfo> wifiInfoList = new ArrayList();
            if (msgArray.length > to) {
                int wifiCount = Integer.valueOf(msgArray[to]);

                from = to + 1;
                to = from + 3 * wifiCount;
                String[] wifiArray = Arrays.copyOfRange(msgArray, from, to);

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
                }
            }

            petGps.setBtsInfoList(btsInfoList);
            petGps.setWifiInfoList(wifiInfoList);
            petGps.setSystemTime(new Date());
            petGps.setPetKey(petId + "," + device);

            // 添加位置处理队列
            LocationTask.dealUD(petGps);
            return;
        }

        // 下发指令应答
        if (ackCmds.contains(cmd)) {
            log.info("设备[{}]应答[{}]。", device, content);

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

    /**
     * 修正时差
     *
     * @param calendar
     * @param offset   (1: 正偏移, -1: 负偏移)
     * @return
     */
    private Date reviseTime(Calendar calendar, int offset) {
        // 1、取得时间偏移量：
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        // 2、取得夏令时差：
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        // 3、从本地时间里扣除这些差量，即可以取得UTC时间：
        calendar.add(Calendar.MILLISECOND, offset * (zoneOffset + dstOffset));

        return calendar.getTime();
    }

    /**
     * 当日初始化数据
     *
     * @param deviceId
     * @return
     */
    private int initStep(long deviceId) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date start = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        List<InitData> dataList = initDataJpa.findByDeviceIdAndDatetimeBetween(deviceId, start, end);
        int result = dataList.stream().collect(Collectors.summingInt(x -> x.getStep()));

        return result;
    }

    /**
     * 判断两个日期是否同一天
     *
     * @param d1
     * @param d2
     * @return
     */
    private boolean inSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {

            return false;
        }

        String str1 = DateUtil.dateToString(d1, "%1$tY-%1$tm-%1$td");
        String str2 = DateUtil.dateToString(d2, "%1$tY-%1$tm-%1$td");

        return str1.equals(str2);
    }

    /**
     * 保存数据
     *
     * @param gpsCur
     */
    private void toInitData(PetGpsCur gpsCur){
        InitData initData = new InitData();
        initData.setDatetime(gpsCur.getGpsTime());
        initData.setDeviceId(gpsCur.getId());
        initData.setStep(gpsCur.getStep());

        initDataJpa.save(initData);
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
