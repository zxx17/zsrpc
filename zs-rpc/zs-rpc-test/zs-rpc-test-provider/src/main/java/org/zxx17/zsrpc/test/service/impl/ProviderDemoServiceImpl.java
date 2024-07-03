package org.zxx17.zsrpc.test.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.annotation.RpcService;
import org.zxx17.zsrpc.test.api.DemoService;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
@RpcService(interfaceClass = DemoService.class,
        interfaceClassName = "org.zxx17.zsrpc.test.api.DemoService",
        version = "1.0.0",
        group = "test")
public class ProviderDemoServiceImpl implements DemoService{
    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }

}
