package org.zxx17.zsrpc.proxy.jdk;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.proxy.api.BaseProxyFactory;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

import java.lang.reflect.Proxy;

/**
 * JDK反射动态代理实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
@SPIClass
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(JdkProxyFactory.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        logger.info("use jdk-proxy");
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }



}
