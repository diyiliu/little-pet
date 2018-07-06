package com.dyl.gw.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description: PetEncoder
 * Author: DIYILIU
 * Update: 2018-07-06 09:51
 */
public class PetEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        ByteBuf buf = (ByteBuf) msg;

        if (buf.readableBytes() > 0){

            out.writeBytes(buf);
        }
    }
}
