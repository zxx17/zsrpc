package org.zxx17.zsrpc.test.consumer.handler;

import com.alibaba.fastjson2.JSONObject;
import org.zxx17.zsrpc.consumer.common.RpcConsumer;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.header.RpcHeaderFactory;
import org.zxx17.zsrpc.protocol.request.RpcRequest;

/**
 * 测试服务消费者.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
public class RpcConsumerHandlerTest {
    public static void main(String[] args) throws Exception {
        RpcConsumer rpcConsumer = RpcConsumer.getInstance();
        rpcConsumer.sendRequest(getRpcRequestProtocol());
        Thread.sleep(2000);
        rpcConsumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();

        RpcRequest request = new RpcRequest();
        request.setClassName("org.zxx17.zsrpc.test.api.DemoService");
        request.setGroup("test");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"zxx17"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);

        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        protocol.setBody(request);
        return protocol;
    }


}
