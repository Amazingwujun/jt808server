package com.zhitu.jt808server.server.handler;

import lombok.Data;

/**
 * 会话
 *
 * @author Jun
 * @date 2020-03-30 14:08
 */
@Data
public class Session {

    private short seqId;

    private String msisdn;

    public int increaseAndGetMessageId() {
        return ++seqId;
    }
}
