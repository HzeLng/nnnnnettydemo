package com.hzelng.nettydemo.TimeServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author HzeLng
 * @version 1.0
 * @description TimeServerHandler
 * @date 2021/4/29 21:51
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当建立连接并准备生成流量(即发送数据时)时，将调用channelActive（）方法
     *
     * 所以这里 channelActive 应该是 和客户端成功建立连接后
     * 【主动】 向客户端发送消息
     *
     * 并且利用ChannelFuture在消息发送后 关闭连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //ChannelHandlerContext.alloc（）获取当前的ByteBufAllocator并分配一个新的缓冲区，i=4表示初始化容量
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        //ChannelHandlerContext.write（）（和writeAndFlush（））方法返回ChannelFuture
        //ChannelFuture表示尚未发生的I/O操作。这意味着，可能还没有执行任何请求的操作，因为Netty中的所有操作都是异步的
        //所以如果ctx.writeAndFlush后直接 接 ctx.close()，那么可能数据还没发送出去就已经关闭连接了
        final ChannelFuture f = ctx.writeAndFlush(time);
        //这里应该是 添加监听器，如果ChannelFuture f 这个任务动作的写并且发送完毕后
        //就会调用 下面的operationComplete方法，然后再ctx.close()关闭连接，这样更为安全
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        });
        //another simple way : f.addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
