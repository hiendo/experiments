package com.github.hiendo.experiments;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 */
public class TargetServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Channel proxyServerChannel;

    public TargetServerChannelInitializer(Channel proxyServerChannel) {
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.DEBUG),
                new HttpRequestEncoder(),
                new TargetServerChannelHandler(proxyServerChannel));
    }
}