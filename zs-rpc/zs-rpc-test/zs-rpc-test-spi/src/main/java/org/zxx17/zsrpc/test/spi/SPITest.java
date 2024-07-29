package org.zxx17.zsrpc.test.spi;

import org.junit.Test;
import org.zxx17.zsrpc.spi.loader.ExtensionLoader;
import org.zxx17.zsrpc.test.spi.service.SPIService;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
public class SPITest {

    @Test
    public void testSPI() {
        SPIService spiService = ExtensionLoader
                .getExtension(SPIService.class, "spiService");
        String sayHi = spiService.sayHi("Xinxuan");
        System.out.println(sayHi);

    }


}
