package org.zxx17.zsrpc.proxy.javassist;

import javassist.util.proxy.MethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.proxy.api.BaseProxyFactory;
import org.zxx17.zsrpc.proxy.api.ProxyFactory;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

import java.lang.reflect.Method;

/**
 * JavassistProxy实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/28
 **/
@SPIClass
public class JavassistProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    private final Logger logger = LoggerFactory.getLogger(JavassistProxyFactory.class);
    private final javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        try {
            logger.info("use javassist-proxy");
            //设置代理类的父类
            proxyFactory.setInterfaces(new Class[]{clazz});
            proxyFactory.setHandler(new MethodHandler() {
                @Override
                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                    return objectProxy.invoke(self, thisMethod, args);
                }
            });
            // 通过字节码技术动态创建子类实例
            return (T) proxyFactory.createClass().newInstance();
        } catch (Exception e) {
            logger.error("javassist proxy throws exception:{} : {}", e, e.getMessage());
        }
        return null;
    }


}
