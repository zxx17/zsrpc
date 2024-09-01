package org.zxx17.zsrpc.test.single;

import org.junit.Test;
import org.zxx17.zsrpc.provider.RpcSingleServer;

public class RpcSingleServerTest {

    // TODO 提供者的SPI后续完成
    @Test
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880",
                "192.168.118.232:9671", "zookeeper",
                "org.zxx17.zsrpc.test", "cglib");
        singleServer.startNettyServer();
    }

}
