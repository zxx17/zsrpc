package org.zxx17.zsrpc.protocol.response;

import org.zxx17.zsrpc.protocol.base.RpcMessage;

import java.io.Serial;

public class RpcResponse extends RpcMessage {

    @Serial
    private static final long serialVersionUID = 7280572481089177831L;

    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
