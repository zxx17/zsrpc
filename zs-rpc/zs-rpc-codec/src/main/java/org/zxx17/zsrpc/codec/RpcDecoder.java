package org.zxx17.zsrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.zxx17.zsrpc.common.util.SerializationUtils;
import org.zxx17.zsrpc.constant.RpcConstants;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.enumeration.RpcType;
import org.zxx17.zsrpc.protocol.header.RpcHeader;
import org.zxx17.zsrpc.protocol.request.RpcRequest;
import org.zxx17.zsrpc.protocol.response.RpcResponse;
import org.zxx17.zsrpc.serialization.api.Serialization;

import java.util.List;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {

    /**
     * 解码RpcProtocol消息。
     * 当输入流中的字节不足以构成一个完整的RpcProtocol消息时，不进行解码操作；
     * 当magic number不匹配时，抛出IllegalArgumentException异常；
     * 根据消息类型、序列化类型、消息体长度等信息，解码出RpcRequest或RpcResponse消息，并封装到RpcProtocol中。
     *
     * @param ctx    ChannelHandlerContext，用于通道的I/O操作。
     * @param in     ByteBuf，输入流，包含待解码的字节。
     * @param out    List<Object>，输出列表，解码后的RpcProtocol消息将添加到此列表中。
     * @throws Exception 如果解码过程中遇到异常。
     */
    @Override
    public final void decode(ChannelHandlerContext ctx,
                             ByteBuf in, List<Object> out) throws Exception {
        // 检查输入流字节是否足够构成一个消息头
        if (in.readableBytes() < RpcConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex(); // 标记当前读取位置，以便在需要时回溯

        // 读取并校验magic number
        short magic = in.readShort();
        if (magic != RpcConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        // 读取消息类型、状态和请求ID
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        // 读取并解析序列化类型
        ByteBuf serializationTypeByteBuf = in.readBytes(SerializationUtils.MAX_SERIALIZATION_TYPE_COUNR);
        String serializationType = SerializationUtils.subString(serializationTypeByteBuf.toString(CharsetUtil.UTF_8));

        // 读取消息体长度
        int dataLength = in.readInt();
        // 如果输入流中的字节不足以构成消息体，则重置读取位置，等待更多字节
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // 读取消息体
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // 根据消息类型获取RpcType枚举值
        RpcType msgTypeEnum = RpcType.findByType(msgType);
        if (msgTypeEnum == null) {
            return; // 如果无法识别消息类型，则不进行解码
        }

        // 创建RpcHeader对象，并设置相关字段
        RpcHeader header = new RpcHeader();
        header.setMagic(magic);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLength);

        // 根据消息类型解码消息体，并封装到RpcProtocol中
        //TODO Serialization是扩展点
        Serialization serialization = getJdkSerialization();
        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO: 处理心跳消息
                break;
        }
    }

}
