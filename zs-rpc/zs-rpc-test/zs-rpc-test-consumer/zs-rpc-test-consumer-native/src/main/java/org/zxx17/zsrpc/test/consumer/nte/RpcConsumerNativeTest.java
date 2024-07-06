package org.zxx17.zsrpc.test.consumer.nte;

import org.zxx17.zsrpc.consumer.nte.RpcClient;
import org.zxx17.zsrpc.test.api.DemoService;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public class RpcConsumerNativeTest {

    public static void main(String[] args){
        RpcClient rpcClient = new RpcClient("1.0.0", "test",
                "jdk", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);

        String result = demoService.hello("zxx17");
        System.err.println("返回的结果数据===>>> " + result);
        rpcClient.shutdown();
    }
}