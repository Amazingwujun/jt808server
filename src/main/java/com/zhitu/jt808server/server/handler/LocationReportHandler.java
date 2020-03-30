package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.common.constant.UniversalAckResult;
import com.zhitu.jt808server.server.message.Jt808Header;
import com.zhitu.jt808server.server.message.Jt808Message;
import com.zhitu.jt808server.server.message.Jt808MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 车辆位置信息汇报处理器
 *
 * @author Jun
 * @date 2020-03-30 10:49
 */
@Slf4j
@Order(0)
@Component
public class LocationReportHandler implements MessageHandler {

    @Override
    public void process(ChannelHandlerContext ctx, Jt808Message jt808Message) {
        log.info("位置汇报信息:{}", jt808Message);
        Jt808Header header = jt808Message.header();
        int seqId = header.getSequenceId();

        //返回消息响应
        Jt808Message response = Jt808MessageFactory
                .universalResponse(MessageId.LOCATION_REPORT, seqId, UniversalAckResult.SUCCESS);
        ctx.writeAndFlush(response);
    }

    @Override
    public boolean support(MessageId messageId) {
        return MessageId.LOCATION_REPORT == messageId;
    }
}
