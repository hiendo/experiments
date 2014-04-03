package com.github.hiendo.experiments;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 *
 */
public class BasicAuthHandler extends ChannelInboundHandlerAdapter {
    final static Logger logger = LoggerFactory.getLogger(BasicAuthHandler.class);
    final static Set<String> validatedSession = new HashSet<>();
    private EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(10);

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        logger.info("Reading in HttpProxyServerChannelHandler");

        HttpRequest request = (HttpRequest) msg;
        final String authCode = request.headers().get("Authorization");
        if (authCode == null) {
            handleFailAuth(ctx);
            return;
        }

        if (validatedSession.contains(authCode)) {
            ctx.fireChannelRead(msg);
            return;
        }

        io.netty.util.concurrent.Future<Boolean> validAuthFuture = executorGroup.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                // @todo perform real authentication here
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (authCode.equals("secret")) {
                    validatedSession.add(authCode);
                    return true;
                } else {
                    return false;
                }
            }
        });

        validAuthFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<Boolean>>() {
            @Override
            public void operationComplete(io.netty.util.concurrent.Future<Boolean> future) throws Exception {
                if (future.isSuccess() && future.get().equals(true)) {
                    ctx.fireChannelRead(msg);
                } else {
                    handleFailAuth(ctx);
                }
            }
        });
    }

    private void handleFailAuth(ChannelHandlerContext ctx) {
        // @todo write http response with unauthorized
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in target auth handling", cause);
        ctx.close();
    }
}
