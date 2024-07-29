package org.zxx17.zsrpc.spi.factory;

import org.zxx17.zsrpc.spi.annonation.SPI;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
@SPI("spi")
public interface ExtensionFactory<T> {

    T getExtension(String key, Class<T> clazz);
}
