package org.zxx17.zsrpc.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务提供者一端执行异步任务.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
public class ServerThreadPool {

    private static final ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(16, 16,
                600L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(65536));
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public static void shutdown(){
        threadPoolExecutor.shutdown();
    }

}
