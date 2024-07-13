package org.zxx17.zsrpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.helper.RpcServiceHelper;
import org.zxx17.zsrpc.common.threadpool.ClientThreadPool;
import org.zxx17.zsrpc.consumer.common.handler.RpcConsumerHandler;
import org.zxx17.zsrpc.consumer.common.helper.RpcConsumerHandlerHelper;
import org.zxx17.zsrpc.consumer.common.initializer.RpcConsumerInitializer;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.meta.ServiceMeta;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.proxy.api.consumer.Consumer;
import org.zxx17.zsrpc.proxy.api.future.RpcFuture;
import org.zxx17.zsrpc.registry.api.RegistryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
public class RpcConsumer implements Consumer {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private static volatile RpcConsumer instance;
    private static final Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    public static RpcConsumer getInstance() {
        if (instance == null) {
            synchronized (RpcConsumer.class) {
                if (instance == null) {
                    instance = new RpcConsumer();
                }
            }
        }
        return instance;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        ClientThreadPool.shutdown();
    }

    /**
     * 向服务提供者发送请求 并获取响应
     */
    @Override
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol,
                                 RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object[] params = request.getParameters();
        int invokerHashCode = (params == null || params.length == 0) ? serviceKey.hashCode() : params[0].hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMeta != null) {
            RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
            //缓存中无RpcClientHandler
            if (handler == null) {
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            } else if (!handler.getChannel().isActive()) {
                //缓存中存在RpcClientHandler，但不活跃
                handler.close();
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            }
            return handler.sendRequest(protocol, request.getAsync(), request.getOneway());
        }
        return null;
    }

    /**
     * 创建连接并返回RpcClientHandler
     */
    private RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (channelFuture.isSuccess()) {
                logger.info("connect rpc server {} on port {} success.", serviceAddress, port);
            } else {
                logger.error("connect rpc server {} on port {} failed.", serviceAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }


}
