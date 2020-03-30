package com.zhitu.jt808server.server.codec;

import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.server.message.Jt808Message;
import com.zhitu.jt808server.server.message.Jt808MessageFactory;
import com.zhitu.jt808server.utils.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * JT808-2011 解码器
 *
 * @author Jun
 * @date 2020-03-18 14:11
 */
public final class Jt808Decoder extends ByteToMessageDecoder {

    /**
     * 标记是否已经读取了报文头的 0x7e
     */
    private boolean alreadyReadHead0x7e;

    /**
     * 最大报文大小
     */
    private int maxFrameLen;

    private boolean discardingTooLongFrame;

    private int tooLongFrameLen;

    public Jt808Decoder(int maxFrameLen) {
        this.maxFrameLen = maxFrameLen;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Object decoded = decode(in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    private Object decode(ByteBuf buffer) {
        if (!alreadyReadHead0x7e) {
            byte head = buffer.readByte();

            //808协议头必须以 0x7e 开始
            if (head != 0x7e) {
                throw new RuntimeException("解码异常");
            }
            alreadyReadHead0x7e = true;
        } else {
            int frameLen = indexOf(buffer);

            if (frameLen >= 0) {
                alreadyReadHead0x7e = false;

                if (discardingTooLongFrame) {
                    //完成丢弃任务，同时初始化解码器状态
                    discardingTooLongFrame = false;
                    buffer.skipBytes(frameLen + 1);

                    int tooLongFrameLen = this.tooLongFrameLen;
                    this.tooLongFrameLen = 0;
                    fail(tooLongFrameLen);
                    return null;
                }

                if (frameLen > maxFrameLen) {
                    buffer.skipBytes(frameLen + 1);
                    fail(frameLen);
                    return null;
                }

                //直接跳过 delimiter
                byte[] frame = new byte[frameLen];
                buffer.readBytes(frame);
                buffer.skipBytes(1);

                return decode2message(frame);
            } else {
                if (!discardingTooLongFrame) {
                    if (buffer.readableBytes() > maxFrameLen) {
                        tooLongFrameLen = buffer.readableBytes();
                        buffer.skipBytes(buffer.readableBytes());
                        discardingTooLongFrame = true;
                        fail(tooLongFrameLen);
                    }
                } else {
                    tooLongFrameLen += buffer.readableBytes();
                    buffer.skipBytes(buffer.readableBytes());
                }
            }
        }

        return null;
    }

    private void fail(long frameLength) {
        if (frameLength > 0) {
            throw new TooLongFrameException(
                    "frame length exceeds " + maxFrameLen +
                            ": " + frameLength + " - discarded");
        } else {
            throw new TooLongFrameException(
                    "frame length exceeds " + maxFrameLen +
                            " - discarding");
        }
    }

    private int indexOf(ByteBuf haystack) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
            if (haystack.getByte(i) == 0x7e) {
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }

    /**
     * 一共三个步骤：
     * <ol>
     *     <li>转义还原</li>
     *     <li>验证校验码</li>
     *     <li>解析消息</li>
     * </ol>
     *
     * @param frame 数据
     * @return {@link Jt808Message}
     */
    private Jt808Message decode2message(byte[] frame) {
        if (frame == null || frame.length < 12) {
            //消息长度最少也是12个字节 - 协议头
            return Jt808MessageFactory.newInvalidMessage(new DecoderException("解包后的消息不合法"));
        }
        //上行消息转义
        byte[] escapeBytes = ByteUtils.inboundEscape(frame);

        //验证校验码
//        if (!ByteUtils.isBccValid(escapeBytes)) {
//            return Jt808MessageFactory.newInvalidMessage(new DecoderException("bcc校验异常"));
//        }

        //获取消息头,并校验剩余消息长度
        Jt808Header header = new Jt808Header(escapeBytes);
        int headerLen = 12;
        if (header.isSplit()) {
            headerLen = 16;
        }
        if (frame.length - headerLen - 1 != header.getBodyLength()) {
            return Jt808MessageFactory.newInvalidMessage(new DecoderException("消息体长度异常"));
        }

        return Jt808MessageFactory.fromDecoder(frame, header);
    }

}
