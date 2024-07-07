package org.zxx17.zsrpc.consumer.nte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.consumer.common.RpcConsumer;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.proxy.api.async.IAsyncObjectProxy;
import org.zxx17.zsrpc.proxy.api.config.ProxyConfig;
import org.zxx17.zsrpc.proxy.api.object.ObjectProxy;
import org.zxx17.zsrpc.proxy.jdk.JdkProxyFactory;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public class RpcClient {
    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    /**
     * 服务版本
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 序列化类型
     */
    private String serializationType;
    /**
     * 超时时间
     */
    private long timeout;
    /**
     * 是否异步调用
     */
    private boolean async;
    /**
     * 是否单向调用
     */
    private boolean oneway;

    public RpcClient(String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    public <T> T create(Class<T> interfaceClass) {
        ProxyFactory proxyFactory = new JdkProxyFactory<T>();
        proxyFactory.init(new ProxyConfig(interfaceClass, serviceVersion, serviceGroup,
                timeout, RpcConsumer.getInstance(), serializationType, async, oneway));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass){
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup,
                serializationType, timeout, RpcConsumer.getInstance(), async, oneway);
    }

    public void shutdown() {
        RpcConsumer.getInstance().close();
    }
}
