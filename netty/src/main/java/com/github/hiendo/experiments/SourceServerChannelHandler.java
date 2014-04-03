package com.github.hiendo.experiments;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SourceServerChannelHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = LoggerFactory.getLogger(SourceServerChannelHandler.class);

    private volatile Channel targetServerChannel;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final Channel proxyServerChannel = ctx.channel();
        // Connect to target server
        Bootstrap b = new Bootstrap();
        b.group(proxyServerChannel.eventLoop()).channel(ctx.channel().getClass())
                .handler(new TargetServerChannelInitializer(proxyServerChannel))
                .option(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture targetServerChannelFuture = b.connect("localhost", 8888);

        targetServerChannel = targetServerChannelFuture.channel();
        targetServerChannelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("Channel is now proxied to target channel");
                    proxyServerChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    proxyServerChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        logger.info("Reading in HttpProxyServerChannelHandler");
        if (targetServerChannel.isActive()) {
            logger.info("Writing in HttpProxyServerChannelHandler");
            targetServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (targetServerChannel != null) {
            closeOnFlush(targetServerChannel);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        targetServerChannel.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in proxy server handling", cause);
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
