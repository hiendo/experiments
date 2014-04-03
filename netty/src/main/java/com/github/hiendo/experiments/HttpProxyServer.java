package com.github.hiendo.experiments;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class HttpProxyServer {
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public HttpProxyServer(int port) {
        this.port = port;
    }

    public ChannelFuture start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new SourceServerChannelInitializer())
                .childOption(ChannelOption.AUTO_READ, false)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        Channel bindChannel = b.bind(port).sync().channel();
        return bindChannel.closeFuture();
    }

    public void stop() throws Exception {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        final HttpProxyServer httpProxyServer = new HttpProxyServer(port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    httpProxyServer.stop();
                } catch (Exception e) {
                }
            }
        });

        ChannelFuture closeFuture = httpProxyServer.start();
        closeFuture.sync();
    }
}
