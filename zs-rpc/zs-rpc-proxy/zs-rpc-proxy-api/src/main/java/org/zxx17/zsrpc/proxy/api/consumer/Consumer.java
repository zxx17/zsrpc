package org.zxx17.zsrpc.proxy.api.consumer;

import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.proxy.api.future.RpcFuture;
import org.zxx17.zsrpc.registry.api.RegistryService;

/**
 * 消费者接口.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public interface Consumer {

    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception;

}
