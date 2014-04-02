package com.github.hiendo.experiments;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HttpProxyServerAuthHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = LoggerFactory.getLogger(HttpProxyServerAuthHandler.class);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        logger.info("Reading in HttpProxyServerChannelHandler");
        boolean auth = true;
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            // @todo perform real authentication here
            String authCode = request.headers().get("authCode");
            if (authCode == null || !authCode.equals("validated")) {
                auth = false;
            }
        }

        if (!auth) {
            // @todo write http response with unauthorized
            ctx.channel().close();
        } else {
            ctx.fireChannelActive();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
