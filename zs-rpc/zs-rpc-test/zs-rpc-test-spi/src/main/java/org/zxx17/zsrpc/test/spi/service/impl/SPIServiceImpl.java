package org.zxx17.zsrpc.test.spi.service.impl;

import org.zxx17.zsrpc.spi.annonation.SPIClass;
import org.zxx17.zsrpc.test.spi.service.SPIService;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
@SPIClass
public class SPIServiceImpl implements SPIService {

    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }
}
