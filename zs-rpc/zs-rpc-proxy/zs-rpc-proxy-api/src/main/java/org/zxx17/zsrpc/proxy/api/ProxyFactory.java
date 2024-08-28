package org.zxx17.zsrpc.proxy.api;

import org.zxx17.zsrpc.constant.RpcConstants;
import org.zxx17.zsrpc.proxy.api.config.ProxyConfig;
import org.zxx17.zsrpc.spi.annonation.SPI;

import java.lang.reflect.Proxy;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/7
 **/
@SPI(RpcConstants.DEFAULT_REFLECT_TYPE)
public interface ProxyFactory {

    /**
     * 获取代理对象
     */
    <T> T getProxy(Class<T> clazz);

    /**
     * 默认的初始化方法
     */
    default <T> void init(ProxyConfig<T> config){}


}
