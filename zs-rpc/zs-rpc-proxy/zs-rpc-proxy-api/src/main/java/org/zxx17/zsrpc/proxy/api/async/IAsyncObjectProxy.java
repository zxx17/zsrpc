package org.zxx17.zsrpc.proxy.api.async;

import org.zxx17.zsrpc.proxy.api.future.RpcFuture;

/**
 * 动态代理异步调用.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/7
 **/
public interface IAsyncObjectProxy {

    /**
     * 异步代理对象调用方法
     * @param method 方法名
     * @param args 参数
     * @return rpcFuture
     */
    RpcFuture call(String method, Object... args);

}
