package com.dyl.gw.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: PetEncoder
 * Author: DIYILIU
 * Update: 2018-07-06 09:51
 */

@Slf4j
public class PetEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        ByteBuf buf = (ByteBuf) msg;

        if (buf.readableBytes() > 0){

            buf.markReaderIndex();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.resetReaderIndex();
            log.info("下行;{}", new String(bytes));

            out.writeBytes(buf);
        }
    }
}
