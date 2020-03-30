package com.zhitu.jt808server.server.message;

import com.zhitu.jt808server.utils.ByteUtils;
import io.netty.handler.codec.DecoderResult;

import java.util.Arrays;

/**
 * 808基础报文
 *
 * @author Jun
 * @date 2020-03-19 14:45
 */
public class Jt808Message {
    //@formatter:off
    private static final byte[] FIX_MARK = {0x7e};

    /**
     * 原始报文，不含前后标识位且为非转义报文，格式如下:
     * <pre>
     * 消息头 | 校验码 | 校验码
     * </pre>
     */
    private byte[] original;
    private Jt808Header jt808Header;
    private byte[] body; //消息体
    private DecoderResult decoderResult;

    //@formatter:on

    public Jt808Message(Jt808Header jt808Header, byte[] body) {
        this(null, jt808Header, body, DecoderResult.SUCCESS);
    }

    /**
     * 构造用于网关的808协议对象，仅对消息头做解析，消息体的处理由具体的 {@link com.zhitu.jt808server.server.handler.MessageHandler} 处理
     *
     * @param original    原始报文
     * @param jt808Header 消息头
     */
    public Jt808Message(byte[] original, Jt808Header jt808Header) {
        this(original, jt808Header, null, DecoderResult.SUCCESS);
    }

    public Jt808Message(Jt808Header jt808Header, byte[] body, DecoderResult decoderResult) {
        this.jt808Header = jt808Header;
        this.body = body;
        this.decoderResult = decoderResult;
    }

    public Jt808Message(byte[] original, Jt808Header jt808Header, byte[] body, DecoderResult decoderResult) {
        this.original = original;
        this.jt808Header = jt808Header;
        this.body = body;
        this.decoderResult = decoderResult;
    }

    /**
     * 由 {@link #original} 中抽取出消息体数据
     */
    public byte[] extractPayloadFromOriginal() {
        int bodyLength = jt808Header.getBodyLength();
        int headLen = jt808Header.length();

        return Arrays.copyOfRange(original, headLen, headLen + bodyLength);
    }

    /**
     * 由 {@link #original} 得出最终的报文
     */
    public byte[] getBody() {
        if (body != null) {
            return body;
        }
        if (original == null) {
            throw new IllegalArgumentException("original 不能为空");
        }

        //转义
        byte[] outboundEscape = ByteUtils.outboundEscape(original);

        //合并结果集
        return ByteUtils.byteMerge(
                FIX_MARK,
                outboundEscape,
                FIX_MARK
        );
    }

    public byte[] getOriginal() {
        return original;
    }

    public Jt808Header header() {
        return jt808Header;
    }

    public void setOriginal(byte[] original) {
        this.original = original;
    }

    public DecoderResult decoderResult() {
        return decoderResult;
    }

    /**
     * 打印 {@link #jt808Header} 及 {@link #original} 即可
     */
    @Override
    public String toString() {
        return "Jt808Message{" +
                ", jt808Header=" + jt808Header +
                "original=" + ByteUtils.byteArrayToHexString(original) +
                '}';
    }
}
