package org.zxx17.zsrpc.consumer.nte;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.exception.RegistryException;
import org.zxx17.zsrpc.consumer.common.RpcConsumer;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.proxy.api.async.IAsyncObjectProxy;
import org.zxx17.zsrpc.proxy.api.config.ProxyConfig;
import org.zxx17.zsrpc.proxy.api.object.ObjectProxy;
import org.zxx17.zsrpc.proxy.jdk.JdkProxyFactory;
import org.zxx17.zsrpc.registry.api.RegistryService;
import org.zxx17.zsrpc.registry.api.config.RegistryConfig;
import org.zxx17.zsrpc.registry.zk.ZookeeperRegistryService;

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

    /**
     * 服务注册与发现接口
     */
    private RegistryService registryService;

    public RpcClient(String registryAddress, String registryType,
                     String serviceVersion, String serviceGroup,
                     String serializationType,
                     long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.registryService = this.getRegistryService(registryAddress, registryType);
    }


    private RegistryService getRegistryService(String registryAddress, String registryType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }
        //TODO 后续SPI扩展
        RegistryService registryService = new ZookeeperRegistryService();
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("RpcClient init registry service throws exception:{}", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        ProxyFactory proxyFactory = new JdkProxyFactory<T>();
        proxyFactory.init(new ProxyConfig(interfaceClass, serviceVersion, serviceGroup,
                timeout, registryService, RpcConsumer.getInstance(), serializationType, async, oneway));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup,
                serializationType, timeout, registryService, RpcConsumer.getInstance(), async, oneway);
    }

    public void shutdown() {
        RpcConsumer.getInstance().close();
    }
}
