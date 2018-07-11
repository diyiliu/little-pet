package com.dyl.gw.netty.handler.codec;

import com.diyiliu.plugin.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Description: PetDecoder
 * Author: DIYILIU
 * Update: 2018-07-06 09:51
 */

@Slf4j
public class PetDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if (in.readableBytes() < 31){

            return;
        }
        in.markReaderIndex();

        byte start = in.readByte();

        // 厂商
        in.readShort();

        byte mark = in.readByte();

        // 设备ID
        in.readBytes(new byte[15]);

        mark = in.readByte();

        // 流水号
        in.readInt();

        mark = in.readByte();

        // 内容长度
        byte[] lenBytes = new byte[4];
        in.readBytes(lenBytes);

        String lenStr = new String(lenBytes);
        int length = Integer.parseInt(lenStr, 16);
        if (in.readableBytes() < length + 2){

            in.resetReaderIndex();
            return;
        }

        mark = in.readByte();

        byte[] content = new byte[length];
        in.readBytes(content);

        byte end = in.readByte();

        in.resetReaderIndex();
        byte[] bytes = new byte[31 + length];
        in.readBytes(bytes);

        log.info("上行;{}", new String(bytes));
        out.add(Unpooled.copiedBuffer(bytes));
    }
}
