package org.zxx17.zsrpc.provider.common.server.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.provider.common.handler.RpcProviderHandler;
import org.zxx17.zsrpc.provider.common.server.api.Server;

import java.util.HashMap;
import java.util.Map;

public class BaseServer implements Server {

    private final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    // 主机域名或者IP地址
    protected String host = "127.0.0.1";

    // 端口号
    protected int port = 27110;

    // 存储的是实体类关系
    protected Map<String, Object> handlerMap = new HashMap<>();


    public BaseServer(String serverAddress){
        if (!StringUtils.isEmpty(serverAddress)){
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }
    }

    /**
     * 启动Netty服务器。
     * 此方法用于配置并启动Netty服务器，监听指定的主机和端口，以提供RPC服务。
     * 使用NIO事件循环组来处理连接接受和读写事件。
     * 通过ChannelInitializer来配置新建立连接的Channel的处理逻辑，包括编解码器和业务处理handler。
     */
    @Override
    public void startNettyServer() {
        // 创建用于接收连接的事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建用于处理连接读写事件的事件循环组
        EventLoopGroup workerGroup  = new NioEventLoopGroup();
        try {
            // 配置服务器启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 设置事件循环组，用于处理连接的接受和读写
            serverBootstrap.group(bossGroup, workerGroup)
                    // 指定使用的Channel类型为NIO服务器Socket通道
                    .channel(NioServerSocketChannel.class)
                    // 配置新建立连接的处理逻辑
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 配置Channel的处理管道，包括添加编解码器和业务处理handler
                            channel.pipeline()
                                    // 添加字符串解码器
                                    //TODO 预留编解码，需要实现自定义协议
                                    .addLast(new StringDecoder())
                                    // 添加字符串编码器
                                    .addLast(new StringEncoder())
                                    // 添加RPC服务提供者处理器
                                    .addLast(new RpcProviderHandler(handlerMap));
                        }
                    })
                    // 配置服务器的其他选项，如连接队列大小和保持活动状态
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定服务器到指定的主机和端口，并同步等待绑定完成
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            // 日志记录服务器启动成功
            logger.info("Server started on {}:{}", host, port);
            // 等待服务器通道关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            // 日志记录服务器启动异常
            logger.error("RPC Server start error", e);
        } finally {
            // 关闭事件循环组，释放资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
