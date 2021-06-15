package com.hzelng.nettydemo.Http.HeartBeat.Client;

import com.hzelng.nettydemo.Http.HeartBeat.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author HzeLng
 * @version 1.0
 * @description HeartbeatEncode
 * @date 2021/5/5 15:15
 *
 * 客户端 编码器
 */
public class HeartbeatEncode  extends MessageToByteEncoder<CustomProtocol> {

    /**
     *消息的前八个字节为 header，剩余的全是 content
     * @param ctx
     * @param msg   待发送的数据实体
     * @param out   缓冲区
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomProtocol msg, ByteBuf out) throws Exception {

        out.writeLong(msg.getId());
        out.writeBytes(msg.getContent().getBytes());


    }
}
