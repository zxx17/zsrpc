package org.zxx17.zsrpc.codec;

import org.zxx17.zsrpc.serialization.api.Serialization;
import org.zxx17.zsrpc.serialization.jdk.JdkSerialization;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
public interface RpcCodec {

    default Serialization getJdkSerialization(){
        return new JdkSerialization();
    }

}
