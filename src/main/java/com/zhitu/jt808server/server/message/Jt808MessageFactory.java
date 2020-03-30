package com.zhitu.jt808server.server.message;

import com.zhitu.jt808server.common.constant.MessageId;
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

    /**
     * 通用响应构建
     *
     * @param messageId 上行消息的 {@link MessageId}
     * @param seqId     上行消息流水号
     * @param result    {@link com.zhitu.jt808server.common.constant.UniversalAckResult}
     * @return 通用下行响应
     */
    public static Jt808Message universalResponse(MessageId messageId, int seqId, byte result) {
        Jt808Header jt808Header = Jt808Header.universalHeader(MessageId._COMMON_ACK);
        UniversalAck universalAck = new UniversalAck(messageId, seqId, result);
        return Jt808MessageFactory.newMessage(jt808Header, universalAck.toBytes());
    }
}
