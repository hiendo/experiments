package com.github.hiendo.experiments;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TargetServerChannelHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = LoggerFactory.getLogger(SourceServerChannelHandler.class);

    private final Channel proxyServerChannel;

    public TargetServerChannelHandler(Channel proxyServerChannel) {
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        proxyServerChannel.write(msg).addListener(new ChannelFutureListener() {
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        proxyServerChannel.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SourceServerChannelHandler.closeOnFlush(proxyServerChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in target server handling", cause);
        SourceServerChannelHandler.closeOnFlush(ctx.channel());
    }
}
