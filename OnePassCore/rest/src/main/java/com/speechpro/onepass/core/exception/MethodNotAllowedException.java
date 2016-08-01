package com.speechpro.onepass.core.exception;

/**
 * @author volobuev
 * @since 27.07.2016
 */
public class MethodNotAllowedException extends RestException {
    public MethodNotAllowedException(final String message, final String reason) {
        super(message, reason);
    }
}
