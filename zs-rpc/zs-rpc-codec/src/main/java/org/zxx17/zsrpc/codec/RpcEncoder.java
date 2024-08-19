package org.zxx17.zsrpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.zxx17.zsrpc.common.util.SerializationUtils;
import org.zxx17.zsrpc.protocol.RpcProtocol;
import org.zxx17.zsrpc.protocol.header.RpcHeader;
import org.zxx17.zsrpc.serialization.api.Serialization;

import java.nio.charset.StandardCharsets;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {


    /**
     * 将RpcProtocol消息编码为ByteBuf。
     * 此方法负责将消息头和消息体的信息按照特定的协议格式写入ByteBuf中，
     * 以便于后续的网络传输。
     *
     * @param channelHandlerContext Netty的ChannelHandlerContext对象，用于通道的I/O操作。
     * @param msg 待编码的RpcProtocol消息对象，包含消息头和消息体。
     * @param byteBuf 编码后的内容将写入此ByteBuf。
     * @throws Exception 如果编码过程中发生错误，则抛出异常。
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        // 获取Rpc请求头
        RpcHeader header = msg.getHeader();
        // 写入协议头的魔法数
        byteBuf.writeShort(header.getMagic());
        // 写入协议头的消息类型
        byteBuf.writeByte(header.getMsgType());
        // 写入协议头的状态
        byteBuf.writeByte(header.getStatus());
        // 写入协议头的请求ID
        byteBuf.writeLong(header.getRequestId());
        // 获取序列化类型
        String serializationType = header.getSerializationType();
        // 获取序列化器实例
        Serialization serialization = getSerialization(serializationType);
        // 将序列化类型字符串填充至固定长度，并写入ByteBuf
        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));
        // 序列化消息体，并写入ByteBuf的长度
        byte[] data = serialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        // 写入序列化后的消息体
        byteBuf.writeBytes(data);
    }



}
