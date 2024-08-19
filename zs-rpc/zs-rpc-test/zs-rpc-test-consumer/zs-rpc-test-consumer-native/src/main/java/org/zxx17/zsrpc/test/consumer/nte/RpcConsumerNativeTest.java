package org.zxx17.zsrpc.test.consumer.nte;

import org.junit.Before;
import org.junit.Test;
import org.zxx17.zsrpc.consumer.nte.RpcClient;
import org.zxx17.zsrpc.proxy.api.async.IAsyncObjectProxy;
import org.zxx17.zsrpc.proxy.api.future.RpcFuture;
import org.zxx17.zsrpc.test.api.DemoService;

import java.util.concurrent.ExecutionException;

/**
 * 服务消费者整合动态代理调用消费者测试.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public class RpcConsumerNativeTest {

    private RpcClient rpcClient;

    @Before
    public void initRpcClient() {
        rpcClient = new RpcClient("101.91.117.127:19671",
                "zookeeper", "1.0.0",
                "test", "json", 3000, false, false);
    }

    @Test
    public void testInterfaceRpc() {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("zxx17");
        System.out.println("result>>>>>>>>>>>>" + result);
        rpcClient.shutdown();
    }

    @Test
    public void testInterfaceRpcAsync() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy rpcClientAsync = rpcClient.createAsync(DemoService.class);
        RpcFuture call = rpcClientAsync.call("hello", "zxx17");
        Object o = call.get();
        System.out.println("result>>>>>>>>>>>>" + o);
    }


}