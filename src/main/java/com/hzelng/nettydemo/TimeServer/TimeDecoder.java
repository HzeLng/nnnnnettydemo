package com.hzelng.nettydemo.TimeServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author HzeLng
 * @version 1.0
 * @description TimeDecoder
 * @date 2021/5/4 13:20
 *
 * ByteToMessageDecoder内部维护了一个累积缓冲区buf
 * 当接收到新数据时，都会使用内部维护的累积缓冲区调用decode（）方法
 *
 */
public class TimeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //当累积缓冲区中没有足够的数据时，decode（）可以决定不向out添加任何内容
        if (in.readableBytes() < 4) {
            return;
        }

        out.add(in.readBytes(4));
    }
}
