package com.zhitu.jt808server.server;

import com.zhitu.jt808server.server.handler.MessageDispatcher;
import com.zhitu.jt808server.server.handler.Session;
import com.zhitu.jt808server.server.message.Jt808Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求接入及分发
 *
 * @author Jun
 * @date 2020-03-18 15:58
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public final class ServerHandler extends SimpleChannelInboundHandler<Jt808Message> {

    /**
     * channel 群组
     */
    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 客户与 channelId 映射关系
     */
    private static ConcurrentHashMap<String, ChannelId> clientMap = new ConcurrentHashMap<>();

    private MessageDispatcher messageDispatcher;

    public ServerHandler(MessageDispatcher messageDispatcher) {
        Assert.notNull(messageDispatcher,"messageDispatcher can't be null");

        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //获取当前会话
        Session session = (Session) ctx.channel().attr(AttributeKey.valueOf("session")).get();

        //移除 clientMap
        Optional.ofNullable(session)
                .map(Session::getMsisdn)
                .ifPresent(clientMap::remove);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Jt808Message msg) {
        if (msg.decoderResult().isFailure()) {
            exceptionCaught(ctx, msg.decoderResult().cause());
            return;
        }

        messageDispatcher.dispatch(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof DecoderException) {
            log.error(cause.getMessage(), cause);
        } else if (cause instanceof IOException) {
            log.error(cause.getMessage(), cause);
        } else {
            log.error("未知异常", cause);
        }

        //直接关闭连接
        ctx.close();
    }
}
