package com.hzelng.nettydemo.TimeServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @author HzeLng
 * @version 1.0
 * @description TimeClientHandlerV2
 * @date 2021/5/4 12:59
 *
 * 本handler将解决 基于流的传输如TCP/IP 中
 * 数据包粘包问题 因为没有边界 即碎片化问题
 *
 * First Solution：
 * 创建一个内部累积缓冲区 buf
 * 等待所有4个字节都被接收到内部缓冲区buf
 * 然后接收到的数据都累积到buf中
 * 检查buf是否有足够的数据（本例中为4字节），并继续执行实际的业务逻辑
 * 否则，继续存储累积更多的数据，最终所有4个字节都将累积起来，再处理
 *
 * 但不推荐此做法，因为灵活性不够 有可能后续数据是可变字段
 *
 * Second Solution:
 * 可以向ChannelPipeline（通过.addLast()）添加多个ChannelHandler，
 * 因此，您可以将一个单一ChannelHandler拆分为多个模块化ChannelHandler，
 * 以降低应用程序的复杂性。
 * 这里拆解为两个模块化ChaneelHandler:
 * ① TimeDecoder: 专门用来处理碎片问题
 * ② TimeClientHandler: 原来的Handler
 */
public class TimeClientHandlerV2 extends ChannelInboundHandlerAdapter {

    /**
     * 内部累积缓冲区 buf 用来存储接收到的数据信息
     */
    private ByteBuf buf;


    /**
     * 生命周期侦听器方法：可以执行任意（反）初始化任务，只要它不阻塞很长时间
     * 可以不确切地理解为构造函数。
     * @param ctx
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(4);
    }

    /**
     * 生命周期侦听器方法：可以执行任意（反）初始化任务，只要它不阻塞很长时间
     * 可以不确切地理解为析构函数。
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release();
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        //所有接收到的数据都应该累积到buf中
        buf.writeBytes(m);
        m.release();
        //检查buf是否有足够的数据（本例中为4字节），并继续执行实际的业务逻辑
        if (buf.readableBytes() >= 4) {
            long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
        //否则，当更多的数据到达时，Netty将再次调用channelRead（）方法，最终所有4个字节都将累积起来
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
