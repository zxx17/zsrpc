package org.zxx17.zsrpc.proxy.api;

import org.zxx17.zsrpc.proxy.api.config.ProxyConfig;
import org.zxx17.zsrpc.proxy.api.object.ObjectProxy;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/7
 **/
public abstract class BaseProxyFactory<T> implements ProxyFactory {

    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> config) {
        this.objectProxy = new ObjectProxy(
                config.getClazz(),
                config.getServiceVersion(),
                config.getServiceGroup(),
                config.getSerializationType(),
                config.getTimeout(),
                config.getRegistryService(),
                config.getConsumer(),
                config.getAsync(),
                config.getOneway());
    }
}
