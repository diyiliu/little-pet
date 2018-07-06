package com.dyl.gw.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

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

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        String content = new String(bytes);
        log.info(content);
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
            } else if (IdleState.WRITER_IDLE == event.state()) {
                log.warn("写超时...");
            } else if (IdleState.ALL_IDLE == event.state()) {
                log.warn("读/写超时...");
            }
        }
    }
}
