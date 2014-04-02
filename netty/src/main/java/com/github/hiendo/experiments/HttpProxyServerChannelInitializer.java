package com.github.hiendo.experiments;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HttpProxyServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    final static Logger logger = LoggerFactory.getLogger(HttpProxyServerChannelHandler.class);

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        logger.info("Creating channel");
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.DEBUG),
                new HttpRequestDecoder(),
                new HttpProxyServerAuthHandler(),
                new HttpProxyServerChannelHandler());
    }
}