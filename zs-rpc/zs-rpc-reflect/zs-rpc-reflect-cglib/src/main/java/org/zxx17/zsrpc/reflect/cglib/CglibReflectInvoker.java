package org.zxx17.zsrpc.reflect.cglib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.zxx17.zsrpc.reflect.api.ReflectInvoker;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

import java.lang.reflect.Method;

/**
 * cglib反射实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/29
 **/
@SPIClass
public class CglibReflectInvoker implements ReflectInvoker {

    private final Logger logger = LoggerFactory.getLogger(CglibReflectInvoker.class);
    private final Enhancer enhancer = new Enhancer();

    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass,
                               String methodName, Class<?>[] parameterTypes,
                               Object[] parameters) throws Throwable {
        logger.info("use cglib reflect type invoke method...");
        // 生成的代理类继承自serviceClass
        enhancer.setSuperclass(serviceClass);
//        enhancer.setInterfaces(new Class[]{serviceClass});
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                return methodProxy.invokeSuper(proxy, args);
            }
        });
        // 创建代理对象
        Object proxy = enhancer.create();
        // 获取方法
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        // 调用方法
        return method.invoke(proxy, parameters);
    }


}
