package org.zxx17.zsrpc.serialization.api;

import org.zxx17.zsrpc.constant.RpcConstants;
import org.zxx17.zsrpc.spi.annonation.SPI;

/**
 * 默认使用jdk的序列化方式.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
@SPI(RpcConstants.SERIALIZATION_JDK)
public interface Serialization {

    /**
     * 序列化
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> cls);

}
