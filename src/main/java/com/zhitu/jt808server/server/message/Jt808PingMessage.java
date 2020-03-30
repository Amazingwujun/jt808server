package com.zhitu.jt808server.server.message;

import io.netty.handler.codec.DecoderResult;

/**
 * 心跳消息
 *
 * @author Jun
 * @date 2020-03-25 15:44
 */
public class Jt808PingMessage extends Jt808Message {

    /**
     * 心跳请求消息体为空
     *
     * @param jt808Header 消息头
     * @param decoderResult 解码结果
     */
    public Jt808PingMessage(Jt808Header jt808Header, DecoderResult decoderResult) {
        super(jt808Header, null, decoderResult);
    }
}
