package org.zxx17.zsrpc.provider.common.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.helper.RpcServiceHelper;
import org.zxx17.zsrpc.common.threadpool.ServerThreadPool;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.enumeration.RpcStatus;
import org.zxx17.zsrpc.protocol.enumeration.RpcType;
import org.zxx17.zsrpc.protocol.header.RpcHeader;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.protocol.response.RpcResponse;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 接收消息，处理消息，响应消息
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                RpcProtocol<RpcRequest> protocol) throws Exception {
        ServerThreadPool.submit(() -> {
                    RpcHeader header = protocol.getHeader();
                    header.setMsgType((byte) RpcType.RESPONSE.getType());
                    RpcRequest request = protocol.getBody();
                    logger.debug("ZS-RPC Receive Request ID: " + header.getRequestId());
                    RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<RpcResponse>();
                    RpcResponse response = new RpcResponse();
                    try {
                        // 调用服务
                        Object result = handle(request);
                        response.setResult(result);
                        response.setAsync(request.getAsync());
                        response.setOneway(request.getOneway());
                        header.setStatus((byte) RpcStatus.SUCCESS.getCode());
                    } catch (Throwable t) {
                        response.setError(t.toString());
                        header.setStatus((byte) RpcStatus.FAIL.getCode());
                        logger.error("ZS-RPC Server handle request error", t);
                    }
                    responseRpcProtocol.setHeader(header);
                    responseRpcProtocol.setBody(response);
                    ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                logger.debug("ZS-RPC Send Response ID: " + header.getRequestId());
                            } else {
                                logger.error("ZS-RPC Send Response Error ID: " + header.getRequestId());
                            }
                        }
                    });
                }
        );
    }

    /**
     * 调用服务
     * <p>
     * handle()方法接收一个RpcRequest类型的参数，
     * 通过RpcRequest对象结合RpcServiceHelper类的buildServiceKey()方法便可以拼接出从Map中获取指定类实例的Key，
     * 通过Key便可以从Map中获取到服务提供者启动时保存到Map中的类实例。
     * <p>
     * 如果获取到的类实例为空，则直接抛出异常。否则通过调用invokeMethod()方法实现调用真实方法的逻辑，源码如下所示。
     */
    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(
                request.getClassName(),
                request.getVersion(),
                request.getGroup()
        );

        Object targetService = handlerMap.get(serviceKey);
        if (targetService == null) {
            throw new RuntimeException(String.format("ZS-RPC service not exist: %s:%s",
                    request.getClassName(),
                    request.getMethodName()));
        }

        Class<?> serviceClass = targetService.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        if (parameterTypes != null) {
            for (Class<?> parameterType : parameterTypes) {
                logger.debug(parameterType.getName());
            }
        }

        if (parameters != null) {
            for (Object parameter : parameters) {
                logger.debug(parameter.toString());
            }
        }
        return invokeMethod(targetService, serviceClass, methodName, parameterTypes, parameters);

    }

    /**
     * 通过反射技术调用具体的方法
     */
    private Object invokeMethod(Object targetService,
                                Class<?> serviceClass, String methodName,
                                Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(targetService, parameters);
    }


}
