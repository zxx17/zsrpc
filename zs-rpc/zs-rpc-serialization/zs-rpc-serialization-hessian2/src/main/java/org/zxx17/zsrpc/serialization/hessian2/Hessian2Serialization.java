package org.zxx17.zsrpc.serialization.hessian2;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxx17.zsrpc.common.exception.SerializerException;
import org.zxx17.zsrpc.serialization.api.Serialization;
import org.zxx17.zsrpc.spi.annonation.SPIClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

/**
 * Hessian2序列化方式.
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/8/26
 **/
@SPIClass
public class Hessian2Serialization implements Serialization {

    private final Logger logger = LoggerFactory.getLogger(Hessian2Serialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute hessian2 serialize...");
        if (obj == null) {
            throw new SerializerException("The object to be serialized cannot be null.");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);

        try {
            hessian2Output.startMessage();
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            hessian2Output.completeMessage();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        } finally {
            try {
                hessian2Output.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                // Log the exception but do not throw it to avoid interrupting the serialization process.
                logger.error("Failed to close resources after serialization.", e);
            }
        }
    }


    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute hessian2 deserialize...");

        if (data == null) {
            throw new SerializerException("Deserialize data is null");
        }
        if (cls == null) {
            logger.warn("Class parameter is null, which might cause issues later.");
        }

        ByteArrayInputStream byteInputStream = null;
        Hessian2Input hessian2Input = null;
        try {
            byteInputStream = new ByteArrayInputStream(data);
            hessian2Input = new Hessian2Input(byteInputStream);
            hessian2Input.startMessage();
            @SuppressWarnings("unchecked")
            T object = (T) hessian2Input.readObject();
            hessian2Input.completeMessage();
            return object;
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        } finally {
            closeQuietly(byteInputStream);
            closeHessian2Input(hessian2Input);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Log the exception but do not propagate it
                logger.error("Error closing resource", e);
            }
        }
    }

    private void closeHessian2Input(Hessian2Input hessian2Input) {
        if (hessian2Input != null) {
            try {
                hessian2Input.close();
            } catch (IOException e) {
                // Log the exception but do not propagate it
                logger.error("Error closing Hessian2Input", e);
            }
        }
    }


}
