package org.zxx17.zsrpc.codec;

import org.zxx17.zsrpc.serialization.api.Serialization;
import org.zxx17.zsrpc.spi.loader.ExtensionLoader;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
public interface RpcCodec {

    /**
     * 获取序列化类型
     * @param serializationType 序列化类型
     * @return 序列化对象
     */
    default Serialization getJdkSerialization(String serializationType){
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }

}
