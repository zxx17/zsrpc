package org.zxx17.zsrpc.proxy.jdk;


import org.zxx17.zsrpc.proxy.api.BaseProxyFactory;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.proxy.api.consumer.Consumer;
import org.zxx17.zsrpc.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    /**
     * 服务版本号
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 超时时间，默认15s
     */
    private long timeout = 15000;
    /**
     * 服务消费者
     */
    private Consumer consumer;
    /**
     * 序列化类型
     */
    private String serializationType;

    /**
     * 是否异步调用
     */
    private boolean async;
    /**
     * 是否单向调用
     */
    private boolean oneway;

    public JdkProxyFactory() {
    }

    public JdkProxyFactory(String serviceVersion, String serviceGroup, String serializationType, long timeout, Consumer consumer, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }



}
