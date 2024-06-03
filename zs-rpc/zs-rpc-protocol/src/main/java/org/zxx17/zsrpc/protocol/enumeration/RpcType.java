package org.zxx17.zsrpc.protocol.enumeration;

public enum RpcType {


    /**
     * 请求消息
     */
    REQUEST(1),

    /**
     * 响应消息
     */
    RESPONSE(2),

    /**
     * 心跳消息
     */
    HEARTBEAT(3);

    private final int type;

    RpcType(int code) {
        this.type = code;
    }

    public int getType() {
        return type;
    }

    public static RpcType findByType(int type) {
        for (RpcType rpcType : RpcType.values()) {
            if (rpcType.getType() == type) {
                return rpcType;
            }
        }
        return null;
    }

}

