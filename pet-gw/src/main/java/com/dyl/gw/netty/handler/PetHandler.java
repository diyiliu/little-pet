package com.dyl.gw.netty.handler;

import com.diyiliu.plugin.cache.ICache;
import com.diyiliu.plugin.model.MsgPipeline;
import com.diyiliu.plugin.model.SendMsg;
import com.diyiliu.plugin.util.DateUtil;
import com.diyiliu.plugin.util.SpringUtil;
import com.dyl.gw.support.jpa.dto.RawData;
import com.dyl.gw.support.jpa.facade.RawDataJpa;
import com.dyl.gw.support.task.MsgSenderTask;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Description: PetHandler
 * Author: DIYILIU
 * Update: 2018-07-06 09:50
 */

@Slf4j
public class PetHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");
        log.info("[{}]建立连接...", host);

        // 断开连接
        ctx.channel().closeFuture().addListener(
                (ChannelFuture future) -> {
                    if (future.isDone()) {
                        log.info("[{}]断开连接...", host);
                    }
                }
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String text = (String) msg;

        String str = text.substring(1, text.length() - 2);
        String[] array = str.split("\\*");

        String factory = array[0];
        String device = array[1];
        int serial = Integer.parseInt(array[2], 16);

        String content = array[4];
        String[] strArray = content.split(",");
        String cmd = strArray[0];

        switch (cmd) {
            case "INIT":
                String resp1 = "[" + factory + "*" + device + "*" + array[1] + "*0006*INIT,1]";
                toSend(device, cmd, serial, resp1.getBytes());

                break;

            case "LK":
                String resp2 = "[" + factory + "*" + device + "*" + array[1] + "*0016*LK," + DateUtil.dateToString(new Date()).replace(" ", ",") + "]";
                toSend(device, cmd, serial, resp2.getBytes());

                break;

            case "UD":

                log.info("位置数据:[{}]", content);
                break;

            default:
                log.info("未知指令[{}]", cmd);
        }

        // 保持在线
        ICache onlineCacheProvider = SpringUtil.getBean("onlineCacheProvider");
        onlineCacheProvider.put(device, new MsgPipeline(ctx, System.currentTimeMillis()));
        log.info("上行, 设备[{}], 命令[{}],  内容: {}", device, cmd, text);

        // 记录原始指令
        RawData rawData = new RawData();
        rawData.setImei(device);
        rawData.setCmd(cmd);
        rawData.setData(text);
        rawData.setDatetime(new Date());

        RawDataJpa rawDataJpa = SpringUtil.getBean("rawDataJpa");
        rawDataJpa.save(rawData);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器异常[{}]!", cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        String key = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");

        // 心跳处理
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == event.state()) {
                log.warn("读超时...[{}]...", key);
                ctx.close();
            } else if (IdleState.WRITER_IDLE == event.state()) {
                log.warn("写超时...");
            } else if (IdleState.ALL_IDLE == event.state()) {
                log.warn("读/写超时...");
            }
        }
    }

    private void toSend(String device, String cmd, int serial, byte[] content) {
        SendMsg msg = new SendMsg();
        msg.setDevice(device);
        msg.setCmd(cmd);
        msg.setSerial(serial);
        msg.setContent(content);

        MsgSenderTask.send(msg);
    }
}
