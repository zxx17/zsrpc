package org.zxx17.zsrpc.test.consumer.impl;

import org.zxx17.zsrpc.annotation.RpcReference;
import org.zxx17.zsrpc.test.api.DemoService;
import org.zxx17.zsrpc.test.consumer.ConsumerBusinessService;

public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", version = "1.0.0", group = "test")
    private DemoService demoService;

}
