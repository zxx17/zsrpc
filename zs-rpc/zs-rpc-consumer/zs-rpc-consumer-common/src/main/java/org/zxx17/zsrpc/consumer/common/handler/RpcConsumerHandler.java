package org.zxx17.zsrpc.consumer.common.handler;

import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.consumer.common.context.RpcContext;
import org.zxx17.zsrpc.consumer.common.future.RpcFuture;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.header.RpcHeader;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.protocol.response.RpcResponse;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定义了Channel类型的成员变量channel和SocketAddress类型的成员变量remotePeer，
 * 提供一个获取channel成员变量的getChannel()方法和一个获取remotePeer成员变量的getRemotePeer()方法，两个成员。
 * <p>
 * channel成员变量会在Netty注册连接的channelRegistered()方法中进行赋值，remotePeer成员变量会在Netty激活连接的channelActive()方法中赋值。
 * 在Netty接收数据的channelRead0()方法中打印服务消费者接收到的数据。
 * 并且在RpcConsumerHandler类中单独定义了发送数据的方法sendRequest()，并对外提供了关闭连接的close()方法。
 * <p>
 * 服务消费者会调用RpcConsumerHandler类中的sendRequest()方法向服务提供者发送数据，
 * 而服务消费者会通过RpcConsumerHandler类中的channelRead0()方法接收服务提供者响应的数据。.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/3
 **/
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);

    /**
     * channel
     */
    private volatile Channel channel;
    /**
     * remotePeer
     */
    private SocketAddress remotePeer;

    /**
     * 存储请求ID和响应对象
     */
    private Map<Long, RpcFuture> pendingRPC = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null) {
            return;
        }
        logger.info("服务消费者接收到的数据===>>>{}", JSONObject.toJSONString(protocol));
        long requestId = protocol.getHeader().getRequestId();
        RpcFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null) {
            rpcFuture.done(protocol);
        }
    }

    /**
     * 发送请求
     *
     * @param protocol 请求内容
     * @param async    是否异步
     * @param oneway   是否单向
     * @return RpcFuture（sync） or null
     */
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway) {
        logger.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        return oneway ? this.sendRequestOneWay(protocol) : async ?
                sendRequestAsync(protocol) : this.sendRequestSync(protocol);
    }

    /**
     * 发送同步请求
     */
    private RpcFuture sendRequestSync(RpcProtocol<RpcRequest> protocol) {
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    /**
     * 发送异步请求
     */
    private RpcFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol) {
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        RpcContext.getContext().setRPCFuture(rpcFuture);
        channel.writeAndFlush(protocol);
        return null;
    }

    /**
     * 发送单向调用请求
     */
    private RpcFuture sendRequestOneWay(RpcProtocol<RpcRequest> protocol) {
        channel.writeAndFlush(protocol);
        return null;
    }


    private RpcFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
        RpcFuture rpcFuture = new RpcFuture(protocol);
        long requestId = protocol.getHeader().getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
    }


    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

}
