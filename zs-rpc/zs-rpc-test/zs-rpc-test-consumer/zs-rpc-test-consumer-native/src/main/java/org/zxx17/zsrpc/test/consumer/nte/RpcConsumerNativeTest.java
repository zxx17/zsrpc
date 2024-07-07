package org.zxx17.zsrpc.test.consumer.nte;

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


    /**
     * testInterfaceRpc()
     */
    @Test
    public void testSync() {
        RpcClient rpcClient = new RpcClient("1.0.0", "test",
                "jdk", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);

        String result = demoService.hello("zxx17");
        System.err.println("返回的结果数据===>>> " + result);
        rpcClient.shutdown();
    }


    @Test
    public void testAsync() throws ExecutionException, InterruptedException {
        RpcClient rpcClient = new RpcClient("1.0.0", "test",
                "jdk", 3000, true, false);
        IAsyncObjectProxy async = rpcClient.createAsync(DemoService.class);
        RpcFuture call = async.call("hello", "zxx17");
        System.err.println("返回的结果数据===>>> " + call.get());
        rpcClient.shutdown();
    }


}