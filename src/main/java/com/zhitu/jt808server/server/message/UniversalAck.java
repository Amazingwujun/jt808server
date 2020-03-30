package com.zhitu.jt808server.server.message;

import com.zhitu.jt808server.common.constant.MessageId;
import com.zhitu.jt808server.utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 平台通用应答消息体
 *
 * @author Jun
 * @date 2020-03-19 15:22
 */
@Data
@AllArgsConstructor
public class UniversalAck implements Jt808body {

    private MessageId messageId;
    private int sequenceId;
    private byte result;

    public byte[] toBytes(){
        byte[] seqIdBytes = ByteUtils.unsignedShortToBytes(sequenceId);
        byte[] msgIdBytes = ByteUtils.unsignedShortToBytes(messageId.value());
        return ByteUtils.byteMerge(seqIdBytes, msgIdBytes, new byte[]{result});
    }
}
