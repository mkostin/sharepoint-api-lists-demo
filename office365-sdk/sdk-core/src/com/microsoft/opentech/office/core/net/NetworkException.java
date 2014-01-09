package com.microsoft.opentech.office.core.net;


/**
 * Authentication credentials required to respond to a authentication
 * challenge are invalid
 */
public class NetworkException extends RuntimeException {

    private static final long serialVersionUID = 319558534317118022L;

    /**
     * Creates a new NetworkException with a <tt>null</tt> detail message.
     */
    public NetworkException() {
        super();
    }

    /**
     * Creates a new NetworkException with the specified message.
     *
     * @param message the exception detail message
     */
    public NetworkException(final String message) {
        super(message);
    }

    /**
     * Creates a new NetworkException with the specified detail message and cause.
     *
     * @param message the exception detail message
     * @param cause the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
     * if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
     */
    public NetworkException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
