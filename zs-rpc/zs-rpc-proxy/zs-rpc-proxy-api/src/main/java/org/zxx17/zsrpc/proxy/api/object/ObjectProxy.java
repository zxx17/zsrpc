package org.zxx17.zsrpc.proxy.api.object;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.header.RpcHeaderFactory;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.proxy.api.consumer.Consumer;
import org.zxx17.zsrpc.proxy.api.future.RpcFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 消费者端动态代理.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/6
 **/
public class ObjectProxy<T> implements InvocationHandler {


    private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);


    /**
     * 接口的Class对象
     */
    private Class<T> clazz;
    /**
     * 服务版本号
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 超时时间，默认15s
     */
    private long timeout = 15000;
    /**
     * 服务消费者
     */
    private Consumer consumer;
    /**
     * 序列化类型
     */
    private String serializationType;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectProxy(Class<T> clazz,
                       String serviceVersion, String serviceGroup,
                       String serializationType, long timeout, Consumer consumer,
                       boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果被调用的方法属于Object类（即基本的equals, hashCode, toString等方法）
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            // 根据方法名分别处理
            // 如果是Object类的未知方法，则抛出异常
            return switch (name) {
                case "equals" ->
                    // 对于equals方法，比较代理对象是否与传入的第一个参数相等
                        proxy == args[0];
                case "hashCode" ->
                    // 对于hashCode方法，返回代理对象的唯一标识码
                        System.identityHashCode(proxy);
                case "toString" ->
                    // 对于toString方法，返回代理对象的字符串表示形式，包括类名、标识码以及代理处理器信息
                        proxy.getClass().getName() + "@" +
                                Integer.toHexString(System.identityHashCode(proxy)) +
                                ", with InvocationHandler " + this;
                default -> throw new IllegalStateException(String.valueOf(method));
            };
        }

        // 构建RPC请求协议头
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<RpcRequest>();
        requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType));

        // 构建RPC请求体
        RpcRequest request = getRpcRequest(method, args);

        // 将构建好的请求体设置到请求协议中
        requestRpcProtocol.setBody(request);

        // 日志输出，用于调试
        logger.debug(method.getDeclaringClass().getName());
        logger.debug(method.getName());

        if (method.getParameterTypes().length > 0) {
            // 输出参数类型信息
            for (int i = 0; i < method.getParameterTypes().length; ++i) {
                logger.debug(method.getParameterTypes()[i].getName());
            }
        }
        if (args != null) {
            // 输出参数值信息
            for (Object arg : args) {
                logger.debug(arg.toString());
            }
        }

        // 发送RPC请求，并获取一个RPCFuture对象，用于异步等待响应
        RpcFuture rpcFuture = this.consumer.sendRequest(requestRpcProtocol);

        // 根据RPCFuture对象获取响应结果
        // 如果rpcFuture为null，返回null
        // 如果设置了超时时间timeout，等待最多timeout毫秒获取结果
        // 否则，无限制地等待结果
        return rpcFuture == null ? null : timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get();
    }

    /**
     * 构建RPC请求
     */
    private RpcRequest getRpcRequest(Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        // 设置服务版本
        request.setVersion(this.serviceVersion);
        // 设置方法所属类的全名
        request.setClassName(method.getDeclaringClass().getName());
        // 设置方法名
        request.setMethodName(method.getName());
        // 设置方法参数类型
        request.setParameterTypes(method.getParameterTypes());
        // 设置服务组
        request.setGroup(this.serviceGroup);
        // 设置方法参数
        request.setParameters(args);
        // 设置异步标志
        request.setAsync(async);
        // 设置单向调用标志
        request.setOneway(oneway);
        return request;
    }


}
