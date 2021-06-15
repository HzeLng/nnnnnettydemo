package com.hzelng.nettydemo.DiscardServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;



import java.util.Properties;

/**
 * @author HzeLng
 * @version 1.0
 * @description CustomServerHandler
 * @date 2021/4/29 16:28
 *
 * ChannelHandler是Netty中 核心处理业务逻辑
 * 这里继承一个ChannelInboundHandlerAdapter
 * ChannelInboundHandlerAdapter 实现了ChannelInboundHandler
 * 通过继承它 自定义 核心处理逻辑
 */
public class CustomServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每当 从 客户机 接收到新的数据，就会用接收到的消息 调用 此方法来处理
     *
     * Version1:只不过在这里我们因为要discard，所以不做任何处理，只丢弃
     * Version2:Echo Server
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg is "+ msg);
        /**
         * //Version1:Discard the received data silently.
         *
         *
         */
       /* ((ByteBuf) msg).release();*/

        /**
         * Version2: print out the server got into cmd;
         */
        /*ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }*/

        /**
         * Version3: Echo everything the server got;
         *
         * ChannelHandlerContext对象ctx提供各种操作，使您能够触发各种I/O事件和操作
         */
        ctx.write(msg);
        ctx.flush();
        //another simple way  ctx.writeAndFlush(msg);

    }

    /**
     * 当Netty由于I/O错误或处理程序实现由于处理事件时抛出的异常而引发异常时，使用Throwable调用exceptionCaught（）事件处理程序方法
     *
     * 这里可以自定义 对于异常如何处理
     *
     * 在这里我们对于出错直接打印信息然后关闭ctx，ctx是用于传输业务数据。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
