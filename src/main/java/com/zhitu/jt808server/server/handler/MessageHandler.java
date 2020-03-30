package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.server.message.Jt808Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * 报文处理器
 *
 * @author Jun
 * @date 2020-03-19 14:43
 */
public interface MessageHandler {

    /**
     * 具体处理接口，由子类实现
     *
     * @param ctx {@link ChannelHandlerContext}
     * @param jt808Message {@link Jt808Message}
     */
    void process(ChannelHandlerContext ctx, Jt808Message jt808Message);

    /**
     * 用于判断 handler 能否处理对应的消息
     *
     * @param messageId {@link MessageId}
     * @return true 如果 handler 能够处理此 messageId
     */
    boolean support(MessageId messageId);
}
