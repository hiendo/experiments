package com.github.hiendo.experiments;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SourceServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    final static Logger logger = LoggerFactory.getLogger(SourceServerChannelHandler.class);

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        logger.info("Creating channel");
        ch.pipeline().addLast(
                //new LoggingHandler(LogLevel.DEBUG),
                new HttpRequestDecoder(),
                new HttpObjectAggregator(900000),
                new SessionValidationHandler(),
                new SourceServerChannelHandler());
    }
}