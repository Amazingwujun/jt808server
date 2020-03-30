package com.zhitu.jt808server.common.constant;

/**
 * 808 message type，808-2011 上下行消息ID一共53条，网关只针对部分消息做出即时响应
 */
public enum MessageId {
    //@formatter:off

    /** 平台通用应答 */
    _COMMON_ACK(0X8001),
    /** 终端通用应答 */
    COMMON_ACK(0X0001),
    /** 终端注册 */
    SIGN_UP(0X0100),
    /** 终端注册应答 */
    _SIGN_UP(0X8100),
    /** 终端心跳 */
    PING(0X0002),

    /** 终端鉴权 */
    SIGN_IN(0X0102),

    /** 位置信息汇报 */
    LOCATION_REPORT(0X0200);

    //@formatter:on

    private final int value;

    MessageId(int value) {
        this.value = value;
    }

    public static MessageId valueOf(int type) {
        for (MessageId t : values()) {
            if (t.value == type) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown message type: " + type);
    }

    public int value() {
        return value;
    }
}
