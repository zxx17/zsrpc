package org.zxx17.zsrpc.consumer.common.context;

import org.zxx17.zsrpc.consumer.common.future.RpcFuture;

/**
 * 管理RPC（远程过程调用）上下文的工具类，主要用于在分布式系统中处理远程调用时的请求和响应.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/5
 **/
public class RpcContext {

    private RpcContext() {
    }

    /**
     * RpcContext实例
     */
    public static final RpcContext AGENT = new RpcContext();

    /**
     * 存放RPCFuture的InheritableThreadLocal
     */
    private static final InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 获取上下文
     * @return RPC服务的上下文信息
     */
    public static RpcContext getContext(){
        return AGENT;
    }
    /**
     * 将RPCFuture保存到线程的上下文
     * @param rpcFuture RPCFuture
     */
    public void setRPCFuture(RpcFuture rpcFuture){
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }
    /**
     * 获取RPCFuture
     */
    public RpcFuture getRPCFuture(){
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }
    /**
     * 移除RPCFuture
     */
    public void removeRPCFuture(){
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }

}
