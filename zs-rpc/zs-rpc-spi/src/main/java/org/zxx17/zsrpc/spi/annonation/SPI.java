package org.zxx17.zsrpc.spi.annonation;

import java.lang.annotation.*;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/29
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {

    /**
     * 默认的实现方式
     */
    String value() default "";

}
