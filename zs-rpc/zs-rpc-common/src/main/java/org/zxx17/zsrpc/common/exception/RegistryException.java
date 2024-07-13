package org.zxx17.zsrpc.common.exception;

import java.io.Serial;

/**
 * .
 *
 * @author Xinxuan Zhuo
 * @version 1.0.0
 * @since 2024/7/13
 **/
public class RegistryException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6948404183247042932L;

    /**
     * Instantiates a new Serializer exception.
     *
     * @param e the e
     */
    public RegistryException(final Throwable e) {
        super(e);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message the message
     */
    public RegistryException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public RegistryException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
