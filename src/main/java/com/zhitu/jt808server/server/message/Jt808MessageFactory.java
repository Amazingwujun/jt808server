package com.zhitu.jt808server.server.message;

import io.netty.handler.codec.DecoderResult;

/**
 * 协议消息工厂
 *
 * @author Jun
 * @date 2020-03-26 09:54
 */
public class Jt808MessageFactory {

    public static Jt808Message newMessage(Jt808Header header, Object payload) {
        switch (header.getMessageId()) {
            case SIGN_UP:
                return null;
            case SIGN_IN:
                return null;
            case PING:
                return new Jt808PingMessage(header, DecoderResult.SUCCESS);
            default:
                throw new IllegalArgumentException("unknown message type: " + header.getMessageId());
        }
    }

    public static Jt808Message newInvalidMessage(Throwable cause) {
        return new Jt808Message(null, null, DecoderResult.failure(cause));
    }
}
