package org.zxx17.zsrpc.protocol;

import org.zxx17.zsrpc.protocol.header.RpcHeader;

import java.io.Serial;
import java.io.Serializable;

public class RpcProtocol<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2839398127626868599L;
    /**
     * 消息头
     */
    private RpcHeader header;

    /**
     * 消息体
     */
    private T body;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
