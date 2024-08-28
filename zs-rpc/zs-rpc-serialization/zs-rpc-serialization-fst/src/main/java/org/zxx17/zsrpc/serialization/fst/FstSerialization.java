package org.zxx17.zsrpc.serialization.fst;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.exception.SerializerException;
import org.zxx17.zsrpc.serialization.api.Serialization;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

/**
 * fst序列化方式.
 * jdk17并不适用
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/26
 **/
@SPIClass
public class FstSerialization implements Serialization {

    private final Logger logger = LoggerFactory.getLogger(FstSerialization.class);


    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute fst serialize...");
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }
        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        return conf.asByteArray(obj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute fst deserialize...");
        if (data == null) {
            throw new SerializerException("deserialize data is null");
        }
        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        return (T) conf.asObject(data);
    }
}
