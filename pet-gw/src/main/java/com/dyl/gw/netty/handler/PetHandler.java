package com.dyl.gw.netty.handler;

import com.diyiliu.plugin.util.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Description: PetHandler
 * Author: DIYILIU
 * Update: 2018-07-06 09:50
 */

@Slf4j
public class PetHandler extends ChannelInboundHandlerAdapter {
    private final AttributeKey<String> attributeKey = AttributeKey.valueOf("PET_KEY");

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
        ByteBuf buf = (ByteBuf) msg;

        // [ + 厂商 + *
        byte[] headerBytes = new byte[4];
        buf.readBytes(headerBytes);
        String header = new String(headerBytes);

        // 设备ID
        byte[] deviceBytes = new byte[15];
        buf.readBytes(deviceBytes);

        String deviceId = new String(deviceBytes);

        // * + 流水号 + * + 长度 + *
        byte[] bytes = new byte[11];
        buf.readBytes(bytes);

        String str1 = new String(bytes);
        String[] strArray1 = str1.split("\\*");

        int serial = Integer.parseInt(strArray1[1], 16);
        int length = Integer.parseInt(strArray1[2], 16);

        byte[] content = new byte[length];
        buf.readBytes(content);
        String str2 = new String(content);
        String[] strArray2 = str2.split(",");

        String cmd = strArray2[0];

        switch (cmd){
            case "INIT":
                String resp1 = header + deviceId + "*" + serial + "*0006*INIT,1]";
                ctx.writeAndFlush(Unpooled.copiedBuffer(resp1.getBytes()));

                break;

            case "LK":
                String resp2 = header + deviceId + "*" + serial + "*0016*LK," + DateUtil.dateToString(new Date()).replace(" ", ",") + "]";
                ctx.writeAndFlush(Unpooled.copiedBuffer(resp2.getBytes()));

                break;

            case "UD":

                log.info("位置数据:[{}]", str2);
                break;

                default:log.info("未知指令[{}]", cmd);
        }

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
}
