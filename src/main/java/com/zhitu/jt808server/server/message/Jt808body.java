package com.zhitu.jt808server.server.message;

/**
 * 808 消息体抽象类
 *
 * @author Jun
 * @date 2020-03-26 15:36
 */
public interface Jt808body {

    /**
     * 返回消息体字节数组数据
     *
     * @return 字节数组数据
     */
    byte[] toBytes();
}
