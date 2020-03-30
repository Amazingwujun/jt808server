package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.common.constant.UniversalAckResult;
import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.server.message.Jt808Message;
import com.zhitu.jt808server.server.message.Jt808body;
import com.zhitu.jt808server.server.message.UniversalAck;
import com.zhitu.jt808server.utils.ByteUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * 心跳处理
 *
 * @author Jun
 * @date 2020-03-26 14:11
 */
@Component
public class PingHandler implements MessageHandler {

    @Override
    public void process(ChannelHandlerContext ctx, Jt808Message jt808Message) {
        Jt808Header reqHeader = jt808Message.header();

        //通用响应
        Jt808Header respHeader = Jt808Header.universalHeader(MessageId._COMMON_ACK, reqHeader.getMsisdn(), reqHeader.getSequenceId(), 0);
        Jt808body body = new UniversalAck(reqHeader.getSequenceId(), MessageId.PING, UniversalAckResult.SUCCESS);

        Jt808Message response = new Jt808Message(respHeader, ByteUtils.byteMerge(respHeader.toBytes(), body.toBytes()));
        ctx.writeAndFlush(response);
    }

    @Override
    public boolean support(MessageId messageId) {
        return MessageId.PING == messageId;
    }
}
