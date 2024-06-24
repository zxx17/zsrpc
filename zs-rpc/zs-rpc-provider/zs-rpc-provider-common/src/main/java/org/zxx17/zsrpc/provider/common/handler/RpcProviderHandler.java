package org.zxx17.zsrpc.provider.common.handler;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.enumeration.RpcType;
import org.zxx17.zsrpc.protocol.header.RpcHeader;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.protocol.response.RpcResponse;

import java.util.Map;

/**
 * 接收消息，处理消息，响应消息
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                RpcProtocol<RpcRequest> protocol) throws Exception {
        logger.info("RPC提供者收到的数据为====>>> " + JSONObject.toJSONString(protocol));
        logger.info("handlerMap中存放的数据如下所示：");
        for(Map.Entry<String, Object> entry : handlerMap.entrySet()){
            logger.info(entry.getKey() + " === " + entry.getValue());
        }
        RpcHeader header = protocol.getHeader();
        RpcRequest request = protocol.getBody();
        //将header中的消息类型设置为响应类型的消息
        header.setMsgType((byte) RpcType.RESPONSE.getType());
        //构建响应协议数据
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<RpcResponse>();
        RpcResponse response = new RpcResponse();
        response.setResult("数据交互成功");
        response.setAsync(request.getAsync());
        response.setOneway(request.getOneway());
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);
        ctx.writeAndFlush(responseRpcProtocol);
    }



}
