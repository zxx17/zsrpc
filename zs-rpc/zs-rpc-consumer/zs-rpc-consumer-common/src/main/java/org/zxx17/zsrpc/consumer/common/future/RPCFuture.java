package org.zxx17.zsrpc.consumer.common.future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.protocol.response.RpcResponse;

import java.io.Serial;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 定义一个RPCFuture类，继承自Java内置的CompletableFuture类，用于处理远程过程调用（RPC）的异步结果。.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/5
 **/
public class RPCFuture extends CompletableFuture<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RPCFuture.class);

    // 同步管理器，用于控制RPCFuture的完成状态。
    private Sync sync;

    // RPC请求协议对象，封装了RPC请求的数据和元信息。
    private RpcProtocol<RpcRequest> requestRpcProtocol;

    // RPC响应协议对象，封装了RPC响应的数据和元信息。初始为null，当RPC调用完成后被设置。
    private RpcProtocol<RpcResponse> responseRpcProtocol;

    // RPC调用开始的时间戳，用于计算响应时间。
    private long startTime;

    // 响应时间阈值，单位毫秒。如果响应时间超过此阈值，将记录警告日志。
    private long responseTimeThreshold = 5000;

    // 构造函数，接收RPC请求协议作为参数，初始化同步管理器和开始时间。
    public RPCFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {
        this.requestRpcProtocol = requestRpcProtocol;
        this.sync = new Sync();
        this.startTime = System.currentTimeMillis();
    }

    // 重写isDone方法，检查RPCFuture是否已完成。
    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    // 重写get方法，等待RPCFuture完成并返回结果。
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        // 获取锁，直到RPCFuture完成。
        sync.acquire(-1);
        // 如果有响应，则返回响应体中的结果；否则返回null。
        if (this.responseRpcProtocol != null) {
            return this.responseRpcProtocol.getBody().getResult();
        } else {
            logger.warn("empty result");
            return null;
        }
    }

    // 重写get方法，等待RPCFuture在指定时间内完成并返回结果，超时则抛出异常。
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

    // 重写isCancelled方法，由于RPCFuture不支持取消操作，抛出不支持的操作异常。
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    // 重写cancel方法，由于RPCFuture不支持取消操作，抛出不支持的操作异常。
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    // 当RPC调用完成时调用此方法，设置响应协议并释放锁。
    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        // 释放锁，表示RPCFuture已完成。
        sync.release(1);
        // Threshold
        // 计算响应时间，如果超过阈值，记录警告日志。
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = "
                    + responseRpcProtocol.getHeader().getRequestId()
                    + ". Response Time = " + responseTime + "ms");
        }
    }

    // 内部类Sync，基于AbstractQueuedSynchronizer实现的同步管理器。
    static class Sync extends AbstractQueuedSynchronizer {

        @Serial
        private static final long serialVersionUID = 4787951535069130716L;

        // 完成状态码。
        private final int done = 1;

        // 待定状态码。
        private final int pending = 0;

        // 尝试获取锁，如果状态为done则返回true，表示已完成。
        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        // 尝试释放锁，如果状态为pending且能成功设置为done则返回true，表示RPCFuture已完成。
        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                return compareAndSetState(pending, done);
            }
            return false;
        }

        // 检查RPCFuture是否已完成。
        public boolean isDone() {
            return getState() == done;
        }
    }

}
