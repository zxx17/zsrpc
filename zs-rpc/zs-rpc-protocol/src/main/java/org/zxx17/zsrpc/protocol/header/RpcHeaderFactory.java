package org.zxx17.zsrpc.protocol.header;

import org.zxx17.zsrpc.common.id.IdFactory;
import org.zxx17.zsrpc.constant.RpcConstants;
import org.zxx17.zsrpc.protocol.enumeration.RpcType;

public class RpcHeaderFactory {

    public static RpcHeader getRequestHeader(String serializationType) {
        RpcHeader header = new RpcHeader();
        long requestId = IdFactory.getId();
        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        header.setMsgType((byte) RpcType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }

}
