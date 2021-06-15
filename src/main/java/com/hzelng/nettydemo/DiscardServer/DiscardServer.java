package com.hzelng.nettydemo.DiscardServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * @author HzeLng
 * @version 1.0
 * @description DiscardServer
 * @date 2021/4/29 16:46
 */
public class DiscardServer {

    private int port;

    static final boolean SSL = System.getProperty("ssl") != null;


    public DiscardServer(int port) {
        this.port = port;
    }

    /**
     * ① NioEventLoopGroup是一个【处理I/O操作】的 多线程事件循环
     *      bossGroup用于接受连接并将接受的连接注册到worker
     *      workerGroup用于处理连接
     * ② ServerBootstrap是一个帮助类，用于设置服务器
     *
     * @throws Exception
     */
    public void run() throws Exception {

        //SSL configuration
        final SslContext sslCtx;
        if(SSL){
            SelfSignedCertificate ssc =  new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }else{
            sslCtx = null;
        }


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //在这里指定使用NioServerSocketChannel类，该类用于 实例化新通道 以 接受传入连接
                    .channel(NioServerSocketChannel.class)
                    /**
                     * ChannelInitializer是一个特殊的处理程序，用于帮助用户【配置】新的自定义的【通道】。
                     * 可以通过添加一些处理程序（如DiscardServerHandler）来配置新通道的ChannelPipeline，以实现自定义的网络应用程序
                     * 随着应用程序变得复杂，很可能会向管道中添加更多处理程序，并最终将这个匿名类提取到顶级类中
                     * 如：
                     *      可以添加1. HttpRequestDecoder，用于解码request
                     *              2. HttpResponseEncoder，用于编码response
                     *              3. aggregator，消息聚合器
                     *                  (512 * 1024)的参数含义是消息合并的数据大小，如此代表聚合的消息内容长度不超过512kb。
                     *              4. 添加我们自己的处理接口
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            if(sslCtx != null ){
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }

                            p.addLast(new CustomServerHandler())
                                          //添加了这个http-code，应该能将channelread0方法中的msg转化为HttpRequest
                                         //.addLast("http-decode",new HttpServerCodec())
                                         //.addLast("decoder",new HttpRequestDecoder())
                                         //.addLast("encoder",new HttpResponseEncoder())
                                         //.addLast("aggregator",new HttpObjectAggregator(512*1024))
                                         ;

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {


        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new DiscardServer(port).run();
    }

}
