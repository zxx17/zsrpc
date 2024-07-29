package org.zxx17.zsrpc.test.spi.service;

import org.zxx17.zsrpc.spi.annonation.SPI;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
@SPI("spiService")
public interface SPIService {

    String sayHi(String name);
}
