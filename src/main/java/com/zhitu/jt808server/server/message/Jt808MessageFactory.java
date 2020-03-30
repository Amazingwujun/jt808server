package com.zhitu.jt808server.server.message;

import io.netty.handler.codec.DecoderResult;

/**
 * 协议消息工厂
 *
 * @author Jun
 * @date 2020-03-26 09:54
 */
public class Jt808MessageFactory {

    public static Jt808Message newMessage(Jt808Header header, byte[] body) {
        return new Jt808Message(header, body);
    }

    public static Jt808Message newInvalidMessage(Throwable cause) {
        return new Jt808Message(null, null, DecoderResult.failure(cause));
    }

    public static Jt808Message fromDecoder(byte[] original, Jt808Header header) {
        return new Jt808Message(original, header);
    }
}
