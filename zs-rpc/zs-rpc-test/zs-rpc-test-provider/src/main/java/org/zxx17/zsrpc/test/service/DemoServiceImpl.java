package org.zxx17.zsrpc.test.service;

import org.zxx17.zsrpc.annotation.RpcService;
import org.zxx17.zsrpc.test.api.DemoService;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "org.zxx17.zsrpc.test.api.DemoService", version = "1.0.0", group = "test")
public class DemoServiceImpl implements DemoService {
}
