package com.dyl.gw.protocol;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.model.SendMsg;
import com.diyiliu.plugin.util.DateUtil;
import com.dyl.gw.support.model.MsgBody;
import com.dyl.gw.support.task.MsgSenderTask;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

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

    public void parse(MsgBody msgBody, ChannelHandlerContext ctx){
        String cmd = msgBody.getCmd();
        String factory = msgBody.getFactory();
        String device = msgBody.getDevice();
        int serial = msgBody.getSerial();

        // 保持在线
        onlineCacheProvider.put(device, new MsgPipeline(ctx, System.currentTimeMillis()));
        log.info("上行, 设备[{}], 指令[{}],  内容: {}", device, cmd, msgBody.getText());

        // 初始化
        if ("INIT".equals(cmd)){
            String resp1 = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0006*INIT,1]";
            respCmd(device, cmd, serial, resp1.getBytes());

            return;
        }

        // 心跳
        if ("LK".equals(cmd)){
            String resp2 = "[" + factory + "*" + device + "*" + String.format("%04X", serial) + "*0016*LK," + DateUtil.dateToString(new Date()).replace(" ", ",") + "]";
            respCmd(device, cmd, serial, resp2.getBytes());

            return;
        }

        // 位置
        if ("UD".equals(cmd)){
            log.info("位置: [{}]", msgBody.getContent());

            return;
        }

        log.warn("未知: {}", msgBody.toString());
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
