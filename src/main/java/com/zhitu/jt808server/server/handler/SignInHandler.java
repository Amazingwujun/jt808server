package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.common.constant.UniversalAckResult;
import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.server.message.Jt808Message;
import com.zhitu.jt808server.server.message.Jt808MessageFactory;
import com.zhitu.jt808server.utils.SessionUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * 终端鉴权处理器
 *
 * @author Jun
 * @date 2020-03-27 10:16
 */
@Component
public class SignInHandler implements MessageHandler {

    @Override
    public void process(ChannelHandlerContext ctx, Jt808Message jt808Message) {
        //抓取手机号
        Jt808Header header = jt808Message.header();
        String msisdn = header.getMsisdn();
        int seqId = header.getSequenceId();
        MessageId messageId = header.getMessageId();

        //获得客户端令牌
        byte[] body = jt808Message.extractBodyFromOriginal();
        String token = new String(body, Charset.forName("gbk"));

        //todo 请求终端鉴权服务，对车辆登入进行校验


        //保存会话消息
        SessionUtils.saveSessionWithChannel(ctx, msisdn);

        //返回消息响应
        Jt808Message response = Jt808MessageFactory.universalResponse(messageId, seqId,
                UniversalAckResult.SUCCESS);
        ctx.writeAndFlush(response);
    }

    @Override
    public boolean support(MessageId messageId) {
        return MessageId.SIGN_IN == messageId;
    }
}
