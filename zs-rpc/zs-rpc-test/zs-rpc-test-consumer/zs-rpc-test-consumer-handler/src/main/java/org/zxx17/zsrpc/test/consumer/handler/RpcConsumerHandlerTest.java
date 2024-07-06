package org.zxx17.zsrpc.test.consumer.handler;

import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.consumer.common.RpcConsumer;
import org.zxx17.zsrpc.consumer.common.callback.AsyncRpcCallBack;
import org.zxx17.zsrpc.consumer.common.context.RpcContext;
import org.zxx17.zsrpc.consumer.common.future.RpcFuture;
import org.zxx17.zsrpc.consumer.common.handler.RpcConsumerHandler;
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
        RpcFuture rpcFuture = rpcConsumer.sendRequest(getRpcRequestProtocol());
//        System.out.println("测试消费者直接请求后直接获取数据===>" + rpcFuture.get());
//        System.out.println("如果是异步调用上面rpcFuture是null，从RpcContext获取结果"+ RpcContext.getContext().getRPCFuture().get());
        System.out.println("如果是单向调用上面都是null");
        Thread.sleep(2000);
        rpcConsumer.close();
    }

    @Test
    public void testAsyncCallBack() throws Exception {
        RpcConsumer rpcConsumer = RpcConsumer.getInstance();
        RpcFuture rpcFuture = rpcConsumer.sendRequest(getRpcRequestProtocol());
        rpcFuture.addAsyncCallBack(new AsyncRpcCallBack() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("异步回调成功===>" + result);
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("异步回调失败===>" + throwable);
            }
        });
        Thread.sleep(200);
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
