package com.zhitu.jt808server;

import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

//@SpringBootTest
class Jt808serverApplicationTests {

    @Test
    void contextLoads() {

        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            byte[] bytes = ByteUtils.msisdn2bcd("013548208957");
        }
        System.out.println(System.currentTimeMillis() - start);

        System.out.println(ByteUtils.byteArrayToHexString(ByteUtils.msisdn2bcd("013548208957")));
//        String s = "02 00 00 3C 01 37 18 87 19 54 0C 2B" +
//                " 00 00 00 00 00 00 00 00 02 61 62 98 06 EE 17 80 00 00 00 00 00 00 14 08 05 09 18 02 01 04 00 00 00 00 33 18" +
//                " 2A 4D 30 30 2C 31 35 2C 31 30 33 37 31 31 30 38 37 36 35 34 33 32 31 23 4D";

        String s = "01 00 00 2D 01 35 48 20 89 57 00 0F 00 2C 01 2C 37 30 31 31 31 42 53 4A 2D 41 36 2D 42 44 00 00 00 00 00 00 00 00 00 00 00 30 33 36 35 33 36 34 02 B4 A8 44 31 32 32 30 31 e7";



        String[] s1 = s.split(" ");
        byte[] result = new byte[s1.length];
        for (int i = 0; i < s1.length; i++) {
            result[i] = (byte) (Integer.parseInt(s1[i],16) & 0xff);
        }

        System.out.println(ByteUtils.isBccValid(result));

        System.out.println(ByteUtils.byteArrayToHexString(result));

        Jt808Header jt808Header = new Jt808Header(Arrays.copyOfRange(result,0,13));
        System.out.println(jt808Header);
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
