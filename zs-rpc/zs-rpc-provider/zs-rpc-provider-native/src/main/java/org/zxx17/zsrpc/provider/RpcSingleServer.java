package org.zxx17.zsrpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.provider.common.server.base.BaseServer;
import org.zxx17.zsrpc.common.scanner.server.RpcServiceScanner;

/**
 * 作为使用Java方式，不依赖Spring启动rpc框架的类
 */
public class RpcSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String scanPackage) {
        super(serverAddress);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
        }catch (Exception e) {
            logger.error("ZS-RPC Server init error", e);
        }
    }
}
