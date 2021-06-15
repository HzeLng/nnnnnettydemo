package com.hzelng.nettydemo.Http.HeartBeat.Server;

import com.hzelng.nettydemo.TimeServer.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;

/**
 * @author HzeLng
 * @version 1.0
 * @description HeartBeatServer
 * @date 2021/5/5 15:43
 */
public class HeartBeatServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args) throws Exception{
        // Configure SSL.
        final SslContext sslCtx;
        if(SSL){
            SelfSignedCertificate ssc =  new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }else{
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final TimeServerHandler serverHandler = new TimeServerHandler();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //在这里指定使用NioServerSocketChannel类，该类用于 实例化新通道 以 接受传入连接
                    .channel(NioServerSocketChannel.class)

                    .localAddress(new InetSocketAddress(PORT))
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE,true)

                    .childHandler(new HeartbeatInitializer())

                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
