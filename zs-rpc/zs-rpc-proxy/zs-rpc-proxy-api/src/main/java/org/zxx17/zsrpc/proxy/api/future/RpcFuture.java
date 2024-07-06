package org.zxx17.zsrpc.proxy.api.future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.threadpool.ClientThreadPool;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.protocol.response.RpcResponse;
import org.zxx17.zsrpc.proxy.api.callback.AsyncRpcCallBack;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 定义一个RpcFuture类，继承自Java内置的CompletableFuture类，用于处理远程过程调用（RPC）的异步结果。.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/5
 **/
public class RpcFuture extends CompletableFuture<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);

    private ReentrantLock lock = new ReentrantLock();

    // 同步管理器，用于控制RpcFuture的完成状态。
    private Sync sync;

    // RPC请求协议对象，封装了RPC请求的数据和元信息。
    private RpcProtocol<RpcRequest> requestRpcProtocol;

    // RPC响应协议对象，封装了RPC响应的数据和元信息。初始为null，当RPC调用完成后被设置。
    private RpcProtocol<RpcResponse> responseRpcProtocol;

    // RPC调用开始的时间戳，用于计算响应时间。
    private long startTime;

    // 响应时间阈值，单位毫秒。如果响应时间超过此阈值，将记录警告日志。
    private long responseTimeThreshold = 5000;

    // 异步回调任务列表
    private List<AsyncRpcCallBack> pendingCallBacks = new ArrayList<>();


    /**
     * 构造函数，接收RPC请求协议作为参数，初始化同步管理器和开始时间。
     *
     * @param requestRpcProtocol RPC请求协议对象
     */
    public RpcFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {
        this.requestRpcProtocol = requestRpcProtocol;
        this.sync = new Sync();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 重写isDone方法，检查RpcFuture是否已完成。
     */
    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    /**
     * 重写get方法，等待RpcFuture完成并返回结果。
     *
     * @return 结果对象
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        // 获取锁，直到RpcFuture完成。
        sync.acquire(-1);
        // 如果有响应，则返回响应体中的结果；否则返回null。
        if (this.responseRpcProtocol != null) {
            return this.responseRpcProtocol.getBody().getResult();
        } else {
            logger.warn("empty result");
            return null;
        }
    }

    /**
     * 重写get方法，等待RpcFuture在指定时间内完成并返回结果，超时则抛出异常。
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     */
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // 尝试在指定时间内获取锁，超时则抛出异常。
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (responseRpcProtocol != null) {
                return responseRpcProtocol.getBody().getResult();
            } else {
                logger.warn("empty result");
                return null;
            }
        } else {
            // 超时异常处理，记录请求ID、请求类名和方法名。
            throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId()
                    + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
                    + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
        }
    }

    /**
     * 重写isCancelled方法，由于RpcFuture不支持取消操作，抛出不支持的操作异常。
     */
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    /**
     * 重写cancel方法，由于RpcFuture不支持取消操作，抛出不支持的操作异常。
     *
     * @param mayInterruptIfRunning this value has no effect in this
     *                              implementation because interrupts are not used to control
     *                              processing.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    /**
     * 当RPC调用完成时调用此方法，设置响应协议并释放锁。
     *
     * @param responseRpcProtocol RPC响应协议对象
     */
    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        // 释放锁，表示RpcFuture已完成。
        sync.release(1);
        // 调用invokeCallbacks()方法
        invokeCallbacks();
        // Threshold
        // 计算响应时间，如果超过阈值，记录警告日志。
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = "
                    + responseRpcProtocol.getHeader().getRequestId()
                    + ". Response Time = " + responseTime + "ms");
        }
    }

    /**
     * 异步回调任务执行
     *
     * @param callBackTask 异步回调任务
     */
    private void runCallBack(final AsyncRpcCallBack callBackTask) {
        final RpcResponse res = this.responseRpcProtocol.getBody();
        ClientThreadPool.submit(() -> {
            // 异常响应
            if (res.isError()) {
                callBackTask.onException(new RuntimeException("Response error", new Throwable(res.getError())));
            } else {
                callBackTask.onSuccess(res.getResult());
            }
        });
    }

    /**
     * 添加异步回调任务
     */
    public RpcFuture addAsyncCallBack(AsyncRpcCallBack callBackTask) {
        lock.lock();
        try {
            if (isDone()) {
                runCallBack(callBackTask);
            } else {
                pendingCallBacks.add(callBackTask);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 依次执行pendingCallbacks集合中回调接口的方法
     */
    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncRpcCallBack callback : pendingCallBacks) {
                runCallBack(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 内部类Sync，基于AbstractQueuedSynchronizer实现的同步管理器。
     */
    static class Sync extends AbstractQueuedSynchronizer {

        @Serial
        private static final long serialVersionUID = 4787951535069130716L;

        // 完成状态码。
        private final int done = 1;

        // 待定状态码。
        private final int pending = 0;

        /**
         * 尝试获取锁，如果状态为done则返回true，表示已完成。
         *
         * @param acquires the acquire argument. This value is always the one
         *                 passed to an acquire method, or is the value saved on entry
         *                 to a condition wait.  The value is otherwise uninterpreted
         *                 and can represent anything you like.
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        /**
         * 尝试释放锁，如果状态为pending且能成功设置为done则返回true，表示RpcFuture已完成。
         *
         * @param releases the release argument. This value is always the one
         *                 passed to a release method, or the current state value upon
         *                 entry to a condition wait.  The value is otherwise
         *                 uninterpreted and can represent anything you like.
         */
        @Override
        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                return compareAndSetState(pending, done);
            }
            return false;
        }

        /**
         * 检查RpcFuture是否已完成。
         */
        public boolean isDone() {
            return getState() == done;
        }
    }

}
