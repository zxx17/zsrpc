package org.zxx17.zsrpc.proxy.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.proxy.api.BaseProxyFactory;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

/**
 * Bytebuddy代理实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/27
 **/
@SPIClass
public class ByteBuddyProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    private final Logger logger = LoggerFactory.getLogger(ByteBuddyProxyFactory.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        logger.info("use bytebuddy-proxy");
        try {
            return (T) new ByteBuddy().subclass(Object.class)
                    .implement(clazz)
                    .intercept(InvocationHandlerAdapter.of(objectProxy))
                    .make()
                    .load(ByteBuddyProxyFactory.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            logger.error("bytebuddy proxy throws exception:{} : {}", e, e.getMessage());
        }
        return null;
    }

}
