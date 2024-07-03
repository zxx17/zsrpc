package org.zxx17.zsrpc.protocol.enumeration;

/**
 * 远程调用状态枚举.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
public enum RpcStatus {
    SUCCESS(0),
    FAIL(1);
    private final int code;
    RpcStatus(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
