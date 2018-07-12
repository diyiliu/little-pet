package com.dyl.gw.netty.handler;

import com.diyiliu.plugin.util.SpringUtil;
import com.dyl.gw.protocol.PetDataProcess;
import com.dyl.gw.support.model.MsgBody;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

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

        String str = text.substring(1, text.length() - 1);
        String[] array = str.split("\\*");

        String factory = array[0];
        String device = array[1];
        int serial = Integer.parseInt(array[2], 16);
        int length = Integer.parseInt(array[3], 16);
        String content = array[4];
        String[] strArray = content.split(",");
        String cmd = strArray[0];

        MsgBody msgBody = new MsgBody();
        msgBody.setFactory(factory);
        msgBody.setDevice(device);
        msgBody.setSerial(serial);
        msgBody.setLength(length);
        msgBody.setCmd(cmd);
        msgBody.setContent(content);
        msgBody.setText(text);
        // 数据解析
        PetDataProcess process = SpringUtil.getBean("petDataProcess");
        process.parse(msgBody, ctx);
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
                //ctx.close();
            } else if (IdleState.WRITER_IDLE == event.state()) {
                log.warn("写超时...");
            } else if (IdleState.ALL_IDLE == event.state()) {
                log.warn("读/写超时...");
            }
        }
    }
}
