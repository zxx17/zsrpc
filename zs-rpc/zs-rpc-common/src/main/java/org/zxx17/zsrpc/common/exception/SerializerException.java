package org.zxx17.zsrpc.common.exception;

import java.io.Serial;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/6/24
 */

public final class SerializerException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 6365326469244252639L;

    public SerializerException(final Throwable e) {
        super(e);
    }
    public SerializerException(final String message) {
        super(message);
    }
    public SerializerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
