package com.zhitu.jt808server.utils;

import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 字节相关工具
 *
 * @author Jun
 * @date 2018-11-17 17:19
 */
@Slf4j
public class ByteUtils {

    /**
     * 多字节数组合并
     *
     * @param values 需要合并的自己数组
     * @return 合并后的字节数组
     */
    public static byte[] byteMerge(byte[]... values) {
        int len = 0;
        for (byte[] value : values) {
            len += value.length;
        }

        byte[] result = new byte[len];

        int destPos = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, destPos, value.length);
            destPos += value.length;
        }

        return result;
    }

    /**
     * 获取无符号数据
     *
     * @param values 需要计算的字节数组
     * @return 计算后的结果
     */
    public static int getUnsignedShort(byte[] values) {
        int result;
        if (values.length == 2) {
            result = ((0xff & values[0]) << 8) + (0xff & values[1]);
        } else {
            throw new IllegalArgumentException("不支持长度为" + values.length + "字节数组，仅支持2字节");
        }

        return result;
    }

    /**
     * 将 bcd 码转换成终端手机号
     *
     * @param bcd 8421码
     * @return 手机号
     */
    public static String bcd2msisdn(byte[] bcd) {
        StringBuilder msisdn = new StringBuilder();
        for (byte b : bcd) {
            msisdn.append((b >> 4) & 0b1111).append(b & 0b1111);
        }

        return msisdn.toString();
    }

    /**
     * 将 msisdn 转换为bcd
     *
     * @param msisdn 手机号
     * @return 手机号的8421码
     */
    public static byte[] msisdn2bcd(String msisdn) {
        if (msisdn == null || msisdn.length() != 12) {
            throw new IllegalArgumentException("msisdn 异常");
        }

        byte[] bcd = new byte[6];
        for (int i = 0; i < 6; i++) {
            String head = msisdn.substring(2 * i, 2 * i + 1);
            String tail = msisdn.substring(2 * i + 1, 2 * i + 2);
            int h = Integer.parseInt(head);
            int t = Integer.parseInt(tail);
            bcd[i] = (byte) ((h << 4) + t);
        }

        return bcd;
    }

    /**
     * 将字节转换为时间,仅针对特定长度的字节数据
     *
     * @param values 时间数组
     * @return 时间
     */
    public static LocalDateTime bytesToLocalDateTime(byte[] values) {
        return LocalDateTime.of(
                2000 + values[0], values[1], values[2],
                values[3], values[4], values[5]);
    }


    /**
     * 将字节数组装换为 16 进制的字符串
     *
     * @param source 字节数组
     * @return 16 进制的字符串
     */
    public static String byteArrayToHexString(byte[] source) {
        if (source == null) return null;
        StringBuilder sb = new StringBuilder();
        for (byte b : source) {
            String temp = Integer.toHexString(b & 0xff);
            sb.append(temp.length() == 1 ? "0" + temp : temp);
        }

        return sb.toString();
    }

    /**
     * 上行消息转义,仅支持如下格式:
     * <pre>
     *     消息头 | 消息体 | bcc
     * </pre>
     *
     * @param tar 待转义字节数组
     */
    public static byte[] inboundEscape(byte[] tar) {
        if (tar == null || tar.length == 0) {
            return null;
        }

        //先计算出长度
        int len = 0;
        for (byte b : tar) {
            if (b == 0x7d) {
                ++len;
            }
        }
        if (len == 0) {
            return tar;
        }

        byte[] result = new byte[tar.length - len];
        for (int i = 0, j = 0; i < tar.length; i++, j++) {
            if (tar[i] == 0x7d) {
                byte mark = tar[++i];
                if (mark == 0x01) {
                    result[j] = 0x7d;
                    continue;
                } else if (mark == 0x02) {
                    result[j] = 0x7e;
                    continue;
                } else {
                    throw new DecoderException("异常的报文");
                }
            }

            result[j] = tar[i];
        }

        return result;
    }

    /**
     * 下行消息转义，支持的消息格式如下:
     * <pre>
     *     消息头 | 消息体 | bcc
     * </pre>
     *
     * @param headAndBodyAndBcc 下行消息
     * @return 转义后的数据
     */
    public static byte[] outboundEscape(byte[] headAndBodyAndBcc) {
        int len = 0;
        for (byte b : headAndBodyAndBcc) {
            if (b == 0x7e || b == 0x7d) {
                ++len;
            }
        }
        if (len == 0) {
            return headAndBodyAndBcc;
        }

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

        return escapeBytes;
    }

    /**
     * 用于计算数据帧的bcc码, target 结构如下:
     * <pre>
     *     消息头 | 消息体
     * </pre>
     *
     * @param target 给定的字节
     */
    public static byte bccCalculate(byte[] target) {
        byte bcc = target[0];
        for (int i = 1; i < target.length; i++) {
            bcc ^= target[i];
        }

        return bcc;
    }

    /**
     * bcc合法性校验,target 结构如下:
     * <pre>
     *     消息头 | 消息体 | bcc
     * </pre>
     *
     * @param target 给定的字节
     * @return false 如果bcc码校验异常
     */
    public static boolean isBccValid(byte[] target) {
        byte bcc = target[0];
        for (int i = 1; i < target.length - 1; i++) {
            bcc ^= target[i];
        }

        return bcc == target[target.length - 1];
    }

    /**
     * 将无符号short类型数据(实际上就是int)转换为字节数组
     *
     * @param sequenceId 序列ID
     * @return 转换后的数组
     */
    public static byte[] unsignedShortToBytes(int sequenceId) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((sequenceId & 0xffff) >> 8);
        bytes[1] = (byte) (sequenceId & 0xff);

        return bytes;
    }
}
