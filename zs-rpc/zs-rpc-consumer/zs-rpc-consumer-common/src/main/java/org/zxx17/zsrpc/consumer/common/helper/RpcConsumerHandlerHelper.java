package org.zxx17.zsrpc.consumer.common.helper;

import org.zxx17.zsrpc.consumer.common.handler.RpcConsumerHandler;
import org.zxx17.zsrpc.protocol.meta.ServiceMeta;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务消费者处理器的帮助类，连接服务中心.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/13
 **/
public class RpcConsumerHandlerHelper {

    private static final Map<String, RpcConsumerHandler> rpcConsumerHandlerMap;

    static {
        rpcConsumerHandlerMap = new ConcurrentHashMap<>();
    }

    private static String getKey(ServiceMeta key){
       return key.getServiceAddr().concat("_").concat(String.valueOf(key.getServicePort()));
    }

    public static void put(ServiceMeta key, RpcConsumerHandler value){
        rpcConsumerHandlerMap.put(getKey(key), value);
    }

    public static RpcConsumerHandler get(ServiceMeta key){
        return rpcConsumerHandlerMap.get(getKey(key));
    }

    public static void closeRpcClientHandler(){
        Collection<RpcConsumerHandler> rpcClientHandlers = rpcConsumerHandlerMap.values();
        rpcClientHandlers.forEach(RpcConsumerHandler::close);
        rpcConsumerHandlerMap.clear();
    }

}
