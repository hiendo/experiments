package com.github.hiendo.experiments;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HttpProxyServerChannelHandler extends ChannelInboundHandlerAdapter {

    private volatile Channel targetServerChannel;
    private List<Object> messages = new ArrayList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel proxyServerChannel = ctx.channel();
        // Connect to target server
        Bootstrap b = new Bootstrap();
        b.group(proxyServerChannel.eventLoop()).channel(ctx.channel().getClass())
                .handler(new HttpTargetServerChannelInitializer(proxyServerChannel))
                .option(ChannelOption.AUTO_READ, false);
        //ChannelFuture targetServerChannelFuture = b.connect("localhost", 8888);
        ChannelFuture targetServerChannelFuture = b.connect("google.com", 80);

        targetServerChannel = targetServerChannelFuture.channel();
        targetServerChannelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    synchronized (messages) {
                        for (Object object : messages) {
                            targetServerChannel.writeAndFlush(object);
                        }
                        messages.clear();
                    }
                    // proxyServerChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    proxyServerChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        boolean auth = true;
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            // @todo perform real authentication here
            String authCode = request.headers().get("authCode");
            if (authCode == null || !authCode.equals("validated")) {
                auth = false;
            }
        }

        final Channel proxyServerChannel = ctx.channel();
        if (!auth) {
            // @todo write http response with unauthorized
            proxyServerChannel.close();
        } else {
            synchronized (messages) {
                if (!targetServerChannel.isActive() || !messages.isEmpty()) {
                    messages.add(msg);
                } else {
                    targetServerChannel.writeAndFlush(msg);
                }
            }
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
        ctx.flush();
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
