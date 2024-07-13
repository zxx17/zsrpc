package org.zxx17.zsrpc.test.single;

import org.junit.Test;
import org.zxx17.zsrpc.provider.RpcSingleServer;

public class RpcSingleServerTest {

    @Test
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880",
                "120.26.210.135:2181", "zookeeper",
                "org.zxx17.zsrpc.test", "jdk");
        singleServer.startNettyServer();
    }

}
