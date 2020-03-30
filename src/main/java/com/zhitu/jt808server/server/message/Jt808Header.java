package com.zhitu.jt808server.server.message;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.utils.ByteUtils;
import lombok.Data;

import java.util.Arrays;

/**
 * 固定头
 *
 * @author Jun
 * @date 2020-03-19 14:46
 */
@Data
public class Jt808Header {
    //@formatter:off

    /** 消息id */
    private MessageId messageId;

    /** 手机号 */
    private String msisdn;

    /** 消息流水号 */
    private int sequenceId;

    /** 加密方式 */
    private int encryptType;

    /** 消息体长度 */
    private int bodyLength;

    /** 分包 */
    private boolean isSplit;

    /** 消息总包数 */
    private int splitMessageLen;

    /** 分包序号 */
    private int splitMessageSequenceId;

    //@formatter:on


    public Jt808Header(MessageId messageId, String msisdn, int sequenceId, int encryptType, int bodyLength,
                       boolean isSplit, int splitMessageLen, int splitMessageSequenceId) {
        this.messageId = messageId;
        this.msisdn = msisdn;
        this.sequenceId = sequenceId;
        this.encryptType = encryptType;
        this.bodyLength = bodyLength;
        this.isSplit = isSplit;
        this.splitMessageLen = splitMessageLen;
        this.splitMessageSequenceId = splitMessageSequenceId;
    }

    /**
     * 通用响应构建。平台对车辆的通用响应固定头只需要 <b>messageId</b> 即可构建，其它数据头写死即可
     *
     * @param messageId {@link MessageId}
     * @return 通用响应头
     */
    public static Jt808Header universalHeader(MessageId messageId) {
        return new Jt808Header(messageId, "018812345678", 0, 0, 5,
                false, 0, 0);
    }


    /**
     * 由字节数值转换而来
     *
     * @param source 数据源
     */
    public Jt808Header(byte[] source) {
        from(source);
    }

    /**
     * 直接由字节数组构建
     *
     * @param buf 报文
     */
    private void from(byte[] buf) {
        if (buf == null || buf.length < 12) {
            throw new IllegalArgumentException("808协议头数据源异常");
        }

        //消息ID
        int msgId = ByteUtils.getUnsignedShort(Arrays.copyOfRange(buf, 0, 2));
        messageId = MessageId.valueOf(msgId);

        //消息体属性
        int msgBodyProp = ByteUtils.getUnsignedShort(Arrays.copyOfRange(buf, 2, 4));
        bodyLength = msgBodyProp & 0x3f;
        encryptType = (msgBodyProp >> 10) & 0b111;
        isSplit = ((msgBodyProp >> 13) & 0b1) == 1;

        //终端手机号
        byte[] msisdnBytes = Arrays.copyOfRange(buf, 4, 10);
        msisdn = ByteUtils.bcd2msisdn(msisdnBytes);

        //消息流水号
        sequenceId = ByteUtils.getUnsignedShort(Arrays.copyOfRange(buf, 10, 12));

        //分包消息
        if (isSplit) {
            splitMessageLen = ByteUtils.getUnsignedShort(Arrays.copyOfRange(buf, 12, 14));
            splitMessageSequenceId = ByteUtils.getUnsignedShort(Arrays.copyOfRange(buf, 14, 16));
        }
    }

    /**
     * 将数据转换为字节数组，<b>暂不考虑分包消息</b>
     *
     * @return 解析出来的字节数组
     */
    public byte[] toBytes() {
        byte[] msgIdBytes = ByteUtils.unsignedShortToBytes(messageId.value());
        byte[] msgBodyPropBytes = new byte[2];
        msgBodyPropBytes[0] = (byte) ((encryptType << 2) +
                ((bodyLength >> 8) & 0b11));
        msgBodyPropBytes[1] = (byte) (bodyLength & 0xff);
        byte[] msisdnBytes = ByteUtils.msisdn2bcd(msisdn);

        return ByteUtils.byteMerge(msgIdBytes, msgBodyPropBytes, msisdnBytes);
    }

    /**
     * 返回消息头长度
     */
    public int length() {
        return isSplit ? 16 : 12;
    }
}
