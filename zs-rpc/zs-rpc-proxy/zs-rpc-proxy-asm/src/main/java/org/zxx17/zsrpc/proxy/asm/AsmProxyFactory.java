package org.zxx17.zsrpc.proxy.asm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.proxy.api.BaseProxyFactory;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.proxy.asm.proxy.ASMProxy;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

/**
 * Asm代理实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/27
 **/
@SPIClass
public class AsmProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    private final Logger logger = LoggerFactory.getLogger(AsmProxyFactory.class);

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        logger.info("use asm-proxy");
        try {
            return (T) ASMProxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, objectProxy);
        } catch (Exception e) {
            logger.error("asm proxy throws exception:{} : {}", e, e.getMessage());
        }
        return null;
    }

}
