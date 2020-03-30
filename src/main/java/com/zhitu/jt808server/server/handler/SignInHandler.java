package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.server.message.Jt808Message;
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
public class SignInHandler extends AbstractSessionHandler {

    @Override
    public void process(ChannelHandlerContext ctx, Jt808Message jt808Message) {
        //抓取手机号
        Jt808Header header = jt808Message.header();
        int headerLen = header.length();

        //获得客户端令牌
        byte[] body = jt808Message.extractBodyFromOriginal();
        String token = new String(body, Charset.forName("gbk"));

        //todo 请求终端鉴权服务，对车辆登入进行校验


        //
        saveSessionWithChannel(ctx, header.getMsisdn());

    }

    @Override
    public boolean support(MessageId messageId) {
        return MessageId.SIGN_IN == messageId;
    }
}
