package com.zhitu.jt808server.utils;

import com.zhitu.jt808server.server.handler.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * {@link com.zhitu.jt808server.server.handler.Session} 工具
 *
 * @author Jun
 * @date 2020-03-30 17:49
 */
public class SessionUtils {

    /**
     * 生成 seqId
     *
     * @param ctx {@link ChannelHandlerContext}
     * @return 消息ID
     */
    public static int nextSequenceId(ChannelHandlerContext ctx) {
        Session session = getSession(ctx);
        return session.increaseAndGetMessageId();
    }

    /**
     * 返回客户id
     *
     * @param ctx {@link ChannelHandlerContext}
     * @return clientId
     */
    public static String clientId(ChannelHandlerContext ctx) {
        Session session = getSession(ctx);
        return session.getMsisdn();
    }

    /**
     * 存储当前会话状态
     *
     * @param ctx    {@link ChannelHandlerContext}
     * @param msisdn 客户端id - msisdn
     */
    public static void saveSessionWithChannel(ChannelHandlerContext ctx, String msisdn) {
        Channel channel = ctx.channel();
        AttributeKey<Object> attr = AttributeKey.valueOf("session");
        Session session = new Session();
        session.setMsisdn(msisdn);
        channel.attr(attr).set(session);
    }

    /**
     * 获取客户会话
     *
     * @param ctx {@link ChannelHandlerContext}
     * @return {@link Session}
     */
    private static Session getSession(ChannelHandlerContext ctx) {
        return (Session) ctx.channel().attr(AttributeKey.valueOf("session")).get();
    }
}
