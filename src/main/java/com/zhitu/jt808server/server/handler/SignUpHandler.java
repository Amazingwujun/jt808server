package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.server.message.Jt808Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 车辆注册处理器
 *
 * @author Jun
 * @date 2020-03-30 10:48
 */
@Slf4j
@Component
public class SignUpHandler implements MessageHandler {

    @Override
    public void process(ChannelHandlerContext ctx, Jt808Message jt808Message) {
        log.info("车辆注册消息:{}", jt808Message);
    }

    @Override
    public boolean support(MessageId messageId) {
        return MessageId.SIGN_UP == messageId;
    }
}
