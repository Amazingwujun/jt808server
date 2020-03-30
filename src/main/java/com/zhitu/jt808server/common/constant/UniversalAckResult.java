package com.zhitu.jt808server.common.constant;

/**
 * 平台通用响应结果，jt808-2011 中具有如下五种：
 * <ol>
 *     <li>0:成功/确认</li>
 *     <li>1:失败</li>
 *     <li>2:消息有误</li>
 *     <li>3:不支持</li>
 *     <li>4:报警处理确认</li>
 * </ol>
 *
 * @author Jun
 * @date 2020-03-26 14:31
 */
public interface UniversalAckResult {
    
    byte SUCCESS = 0;
    
    byte FAILURE = 1;
    
    byte ERR_MSG = 2;
    
    byte NOT_SUPPORT = 3;
    
    byte WARNING_PROCESS_ACK = 4;
}
