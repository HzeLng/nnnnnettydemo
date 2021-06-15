package com.hzelng.nettydemo.Http.HeartBeat.Server;

import com.hzelng.nettydemo.Http.HeartBeat.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * @author HzeLng
 * @version 1.0
 * @description ServerHeartBeatSimpleHandler
 * @date 2021/5/5 15:38
 */
public class ServerHeartBeatSimpleHandler extends SimpleChannelInboundHandler<CustomProtocol> {

    private static final ByteBuf HEART_BEAT =  Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new CustomProtocol(123456L,"pong").toString(), CharsetUtil.UTF_8));

    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerNettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    /**
     * 服务端如果5秒内没有收到消息 就会触发,向客户端发送
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;
            if (idleStateEvent.state() == IdleState.READER_IDLE){
                System.out.println("已经5秒没有收到信息！");
                //向客户端发送消息
                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol msg) throws Exception {
        System.out.println("the msg is "+msg.getId()+"and the content is "+msg.getContent());
        System.out.println("the channel is "+ctx.channel());
        System.out.println("and we can see the channel id is "+ctx.channel().id());
        ServerNettySocketHolder.put(msg.getId(),(NioSocketChannel)ctx.channel()) ;
    }
}
