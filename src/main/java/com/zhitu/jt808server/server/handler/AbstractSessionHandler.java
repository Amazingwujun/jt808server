package com.zhitu.jt808server.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 会话管理
 *
 * @author Jun
 * @date 2020-03-30 13:52
 */
abstract class AbstractSessionHandler implements MessageHandler {

    /**
     * 生成 seqId
     *
     * @param ctx {@link ChannelHandlerContext}
     * @return 消息ID
     */
    int nextSequenceId(ChannelHandlerContext ctx) {
        Session session = getSession(ctx);
        return session.increaseAndGetMessageId();
    }

    /**
     * 返回客户id
     *
     * @param ctx {@link ChannelHandlerContext}
     * @return clientId
     */
    String clientId(ChannelHandlerContext ctx) {
        Session session = getSession(ctx);
        return session.getMsisdn();
    }

    /**
     * 存储当前会话状态
     *
     * @param ctx    {@link ChannelHandlerContext}
     * @param msisdn 客户端id - msisdn
     */
    void saveSessionWithChannel(ChannelHandlerContext ctx, String msisdn) {
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
    private Session getSession(ChannelHandlerContext ctx) {
        return (Session) ctx.channel().attr(AttributeKey.valueOf("session")).get();
    }

}
