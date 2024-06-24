package org.zxx17.zsrpc.test.consumer.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.zxx17.zsrpc.codec.RpcDecoder;
import org.zxx17.zsrpc.codec.RpcEncoder;
import org.zxx17.zsrpc.test.consumer.handler.RpcTestConsumerHandler;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder());
        cp.addLast(new RpcDecoder());
        cp.addLast(new RpcTestConsumerHandler());
    }

}
