package com.zhitu.jt808server.server;

import com.zhitu.jt808server.server.handler.MessageDispatcher;
import com.zhitu.jt808server.server.message.Jt808Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.DecoderException;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 请求接入及分发
 *
 * @author Jun
 * @date 2020-03-18 15:58
 */
@Slf4j
@Component
public final class ServerHandler extends SimpleChannelInboundHandler<Jt808Message> {

    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private MessageDispatcher messageDispatcher;

    public ServerHandler(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Jt808Message msg) {
        if (msg.decoderResult().isFailure()) {
            exceptionCaught(ctx, msg.decoderResult().cause());
            return;
        }

        messageDispatcher.dispatch(ctx,msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof DecoderException) {
            log.error(cause.getMessage(), cause);
        } else {
            log.error("未知异常", cause);
        }

        //直接关闭连接
        ctx.close();
    }
}
