package com.hzelng.nettydemo.Http.HeartBeat.Server;

import com.hzelng.nettydemo.Http.HeartBeat.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author HzeLng
 * @version 1.0
 * @description HeartbeatDecoder
 * @date 2021/5/5 15:19
 *
 * 服务端 解码
 */
public class HeartbeatDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        long id = in.readLong() ;
        byte[] bytes = new byte[in.readableBytes()] ;
        in.readBytes(bytes) ;
        String content = new String(bytes) ;
        CustomProtocol customProtocol = new CustomProtocol() ;
        customProtocol.setId(id);
        customProtocol.setContent(content) ;
        out.add(customProtocol) ;
    }
}
