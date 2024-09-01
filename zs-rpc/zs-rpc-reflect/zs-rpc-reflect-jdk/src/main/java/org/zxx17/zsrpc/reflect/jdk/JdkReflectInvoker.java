package org.zxx17.zsrpc.reflect.jdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.reflect.api.ReflectInvoker;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

import java.lang.reflect.Method;

/**
 * JDK反射方法实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/29
 **/
@SPIClass
public class JdkReflectInvoker implements ReflectInvoker {
    private final Logger logger = LoggerFactory.getLogger(JdkReflectInvoker.class);

    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass,
                               String methodName, Class<?>[] parameterTypes,
                               Object[] parameters) throws Throwable {
        logger.info("use jdk reflect type invoke method...");
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }



}
