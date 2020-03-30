package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.server.message.Jt808Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 消息调度
 *
 * @author Jun
 * @date 2020-03-27 14:47
 */
@Slf4j
@Component
public class MessageDispatcher {

    private final List<MessageHandler> messageHandlers;

    public MessageDispatcher(List<MessageHandler> messageHandlers) {
        Assert.notEmpty(messageHandlers, "messageHandlers can't be empty");

        this.messageHandlers = messageHandlers;
        messageHandlers.add(new UnsupportedMessageHandler());
    }

    /**
     * 消息调度
     *
     * @param ctx          {@link ChannelHandlerContext}
     * @param jt808Message {@link Jt808Message}
     */
    public void dispatch(ChannelHandlerContext ctx, Jt808Message jt808Message) {
        for (MessageHandler handler : messageHandlers) {
            if (handler.support(jt808Message.header().getMessageId())) {
                handler.process(ctx, jt808Message);
                return;
            }
        }
    }

    /**
     * 用于处理平台暂不支持或非法的消息类别,此 handler 必须置于 {@link #messageHandlers} 尾部
     */
    private static class UnsupportedMessageHandler implements MessageHandler {

        @Override
        public void process(ChannelHandlerContext ctx, Jt808Message jt808Message) {
            log.error("不支持的消息:{}", jt808Message);
        }

        @Override
        public boolean support(MessageId messageId) {
            return true;
        }
    }
}
