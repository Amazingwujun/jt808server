package com.zhitu.jt808server;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

//@SpringBootTest
class Jt808serverApplicationTests {

    @Test
    void contextLoads() {
        int seqId = 1025;

        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((seqId >>8) & 0b11);
        bytes[1] = (byte) (seqId & 0xff);

        System.out.println(ByteUtils.getUnsignedShort(bytes));
    }

    @Test
    void test1() {
        byte[] headAndBodyAndBcc = {0x30,0x7e,0x08,0x7d,0x55};

        //转义
        int len = 0;
        for (byte b : headAndBodyAndBcc) {
            if (b == 0x7e || b == 0x7d) {
                ++len;
            }
        }
        if (len > 0) {
            byte[] escapeBytes = new byte[headAndBodyAndBcc.length + len];
            for (int i = 0, j = 0; i < headAndBodyAndBcc.length; i++, j++) {
                if (headAndBodyAndBcc[i] == 0x7e) {
                    escapeBytes[j] = 0x7d;
                    escapeBytes[++j] = 0x02;
                    continue;
                }
                if (headAndBodyAndBcc[i] == 0x7d) {
                    escapeBytes[j] = 0x7d;
                    escapeBytes[++j] = 0x01;
                    continue;
                }
                escapeBytes[j] = headAndBodyAndBcc[i];
            }

            headAndBodyAndBcc = escapeBytes;
        }

        //合并结果集
        byte[] bytes = ByteUtils.byteMerge(
                new byte[]{0x7e},
                headAndBodyAndBcc,
                new byte[]{0x7e}
        );

        System.out.println(ByteUtils.byteArrayToHexString(bytes));
    }
}
