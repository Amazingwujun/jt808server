package com.zhitu.jt808server.server.codec;

import com.zhitu.jt808server.server.message.Jt808Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.stereotype.Component;

/**
 * 封包处理
 *
 * @author Jun
 * @date 2020-03-30 10:25
 */
@ChannelHandler.Sharable
@Component
public class Jt808Encoder extends MessageToByteEncoder<Jt808Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Jt808Message msg, ByteBuf out) {
        out.writeBytes(msg.toResponse());
    }
}
