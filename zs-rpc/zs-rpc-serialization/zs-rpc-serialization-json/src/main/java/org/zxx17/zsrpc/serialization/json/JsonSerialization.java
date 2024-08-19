package org.zxx17.zsrpc.serialization.json;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.exception.SerializerException;
import org.zxx17.zsrpc.serialization.api.Serialization;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

import java.io.IOException;
import java.text.SimpleDateFormat;


/**
 * Json序列化方式实现.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */
@SPIClass
public class JsonSerialization implements Serialization {

    private final Logger logger = LoggerFactory.getLogger(JsonSerialization.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    static {
        // 创建一个日期格式化器，用于指定日期和时间的格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置 ObjectMapper 在序列化和反序列化日期时使用的格式
        objMapper.setDateFormat(dateFormat);
        // 设置序列化时只包括非空字段，避免序列化输出中包含 null 值的字段
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 开启缩进输出，使生成的 JSON 格式更易于阅读
        objMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 禁止在序列化完成后自动关闭输出流，这在你希望手动控制流的关闭时有用
        objMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        // 禁止在序列化完成后自动关闭 JSON 内容
        objMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
        // 禁止每次写入值之后自动刷新输出流
        objMapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        // 禁止序列化后关闭可关闭资源
        objMapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
        // 允许序列化空对象（即没有公共字段的对象），否则序列化空对象时会抛出异常
        objMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 允许反序列化时忽略 JSON 中未映射到 Java 类属性的字段
        objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略 JSON 中的 undefined 属性，这在处理某些 JSON 输入时可能会有帮助
        objMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }


    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute json serialize...");
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }
        byte[] bytes = new byte[0];

        try {
            bytes = objMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new SerializerException(e.getMessage(), e);
        }
        return bytes;
    }


    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute json deserialize...");
        if (data == null || data.length == 0) {
            throw new SerializerException("deserialize data is null");
        }
        T obj = null;
        try {
            obj = objMapper.readValue(data, cls);
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        }
        return obj;
    }

}
