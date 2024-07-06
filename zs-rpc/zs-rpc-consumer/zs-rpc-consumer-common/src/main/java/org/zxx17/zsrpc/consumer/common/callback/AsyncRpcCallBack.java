package org.zxx17.zsrpc.consumer.common.callback;

/**
 * 异步RPC请求回调.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public interface AsyncRpcCallBack {

    /**
     * 成功后的回调方法.
     * @param result 返回结果
     */
    void onSuccess(Object result);

    /**
     * 异常调用方法.
     * @param throwable 异常
     */
    void onException(Throwable throwable);
}
