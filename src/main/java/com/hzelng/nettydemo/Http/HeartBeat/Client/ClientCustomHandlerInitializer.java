package com.hzelng.nettydemo.Http.HeartBeat.Client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author HzeLng
 * @version 1.0
 * @description ClientCustomHandlerInitializer
 * @date 2021/5/5 15:35
 */
public class ClientCustomHandlerInitializer extends ChannelInitializer<Channel> {

    /**
     * 通道初始化 加入handler
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //如果10s内 客户端没有发送消息 就会触发 其他设置为0应该是无视
                .addLast(new IdleStateHandler(0,10,0))
                .addLast(new HeartbeatEncode())
                //当客户端（服务器）发送数据时，会先writeandflush出去，然后如果有encode
                //enocde就可以编码，其实这里的编码更像是拆解成 字节流再发送出去
                //应该是可以理解成 通道里 传输的就是字节流
                .addLast(new EchoClientHandler());
    }
}
