package org.zxx17.zsrpc.spi.factory;

import org.zxx17.zsrpc.spi.annonation.SPI;
import org.zxx17.zsrpc.spi.annonation.SPIClass;
import org.zxx17.zsrpc.spi.loader.ExtensionLoader;

import java.util.Optional;


/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
@SPIClass
public class SPIExtensionFactory<T> implements ExtensionFactory<T>{

    @Override
    public T getExtension(final String key, final Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(clazz::isInstance)
                .filter(c -> c.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);
    }
}
