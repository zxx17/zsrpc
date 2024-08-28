package org.zxx17.zsrpc.proxy.asm.proxy;

import org.zxx17.zsrpc.proxy.asm.classloader.ASMClassLoader;
import org.zxx17.zsrpc.proxy.asm.factory.ASMGenerateProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作为代理类需要继承的父类，提供一个静态的newProxyInstance()方法，
 * newProxyInstance里面调用ASMProxyFactory生成字节码二进制流，
 * 然后调用自定义的类加载器来生成Class。最后反射生成代理类的实例，返回对象。.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/28
 **/
public class ASMProxy {

    protected InvocationHandler h;
    //代理类名计数器
    private static final AtomicInteger PROXY_CNT = new AtomicInteger(0);
    private static final String PROXY_CLASS_NAME_PRE = "$Proxy";

    public ASMProxy(InvocationHandler var1) {
        h = var1;
    }

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h) throws Exception {
        //生成代理类Class
        Class<?> proxyClass = generate(interfaces);
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        return constructor.newInstance(h);
    }

    /**
     * 生成代理类Class
     * @param interfaces 接口的Class类型
     * @return 代理类的Class对象
     */
    private static Class<?> generate(Class<?>[] interfaces) throws ClassNotFoundException {
        String proxyClassName = PROXY_CLASS_NAME_PRE + PROXY_CNT.getAndIncrement();
        byte[] codes = ASMGenerateProxyFactory.generateClass(interfaces, proxyClassName);
        //使用自定义类加载器加载字节码
        ASMClassLoader asmClassLoader = new ASMClassLoader();
        asmClassLoader.add(proxyClassName, codes);
        return asmClassLoader.loadClass(proxyClassName);
    }

}
