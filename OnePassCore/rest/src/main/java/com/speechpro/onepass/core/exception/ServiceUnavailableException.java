package com.speechpro.onepass.core.exception;

/**
 * @author volobuev
 * @since 16.05.16
 */
public class ServiceUnavailableException extends RestException {
    public ServiceUnavailableException(String message, String reason) {
        super(message, reason);
    }
}
