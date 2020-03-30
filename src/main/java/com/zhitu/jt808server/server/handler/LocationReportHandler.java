package com.zhitu.jt808server.server.handler;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.server.message.Jt808Message;
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
    }

    @Override
    public boolean support(MessageId messageId) {
        return MessageId.LOCATION_REPORT == messageId;
    }
}
