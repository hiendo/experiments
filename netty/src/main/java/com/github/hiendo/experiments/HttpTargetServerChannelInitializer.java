package com.github.hiendo.experiments;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 */
public class HttpTargetServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Channel proxyServerChannel;

    public HttpTargetServerChannelInitializer(Channel proxyServerChannel) {
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.DEBUG),
                new HttpRequestEncoder(),
                new HttpTargetServerChannelHandler(proxyServerChannel));
    }
}