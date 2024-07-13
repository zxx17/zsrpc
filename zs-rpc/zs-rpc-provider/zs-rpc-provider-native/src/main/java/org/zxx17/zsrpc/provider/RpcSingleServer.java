package org.zxx17.zsrpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.provider.common.server.base.BaseServer;
import org.zxx17.zsrpc.provider.common.scanner.RpcServiceScanner;

/**
 * 作为使用Java方式，不依赖Spring启动rpc框架的类
 */
public class RpcSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String registryAddress,
                           String registryType, String scanPackage, String reflectType) {
        //调用父类构造方法
        super(serverAddress, registryAddress, registryType, reflectType);
        try {
            this.handlerMap = RpcServiceScanner.
                    doScannerWithRpcServiceAnnotationFilterAndRegistryService(this.host, this.port, scanPackage, registryService);
        } catch (Exception e) {
            logger.error("RPC Server init error", e);
        }
    }
}
