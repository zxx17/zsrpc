package org.zxx17.zsrpc.protocol;

import org.zxx17.zsrpc.protocol.header.RpcHeader;
import org.zxx17.zsrpc.protocol.header.RpcHeaderFactory;
import org.zxx17.zsrpc.protocol.request.RpcRequest;

public class TestRpcProtocol {

    public static RpcProtocol<RpcRequest> testGetRpcProtocol(){
        RpcHeader header = RpcHeaderFactory.getRequestHeader("jdk");
        RpcRequest body = new RpcRequest();
        body.setOneway(false);
        body.setAsync(false);
        body.setClassName("org.zxx17.zsrpc.protocol.RpcProtocol");
        body.setMethodName("hello");
        body.setGroup("test");
        body.setParameters(new Object[]{"param"});
        body.setParameterTypes(new Class[]{String.class});
        body.setVersion("1.0.0");
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setBody(body);
        protocol.setHeader(header);
        return protocol;
    }


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            RpcProtocol<RpcRequest> rpcRequestRpcProtocol = testGetRpcProtocol();
            System.out.println(rpcRequestRpcProtocol);
        }
    }

}
