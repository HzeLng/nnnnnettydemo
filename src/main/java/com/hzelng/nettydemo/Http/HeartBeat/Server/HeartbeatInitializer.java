package com.hzelng.nettydemo.Http.HeartBeat.Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author HzeLng
 * @version 1.0
 * @description HeartbeatInitializer
 * @date 2021/5/5 15:48
 */
public class HeartbeatInitializer  extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //五秒没有收到消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(new IdleStateHandler(5, 0, 0))
                .addLast(new HeartbeatDecoder())
                //HeartbeatDecoder在ServerHeartBeatSimpleHandler前面
                //会先经过Decoder然后解码成一个CustomProtocal
                // 再在ServerHeartBeatSimpleHandler里的channelRead0 会被封装成CustomProtocal
                .addLast(new ServerHeartBeatSimpleHandler());
    }
}
