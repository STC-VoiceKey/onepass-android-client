package com.speechpro.onepass.core.exception;

/**
 * @author volobuev
 * @since 20.04.16
 */
public class ForbiddenException extends RestException {
    public ForbiddenException(String message) {
        super(message);
    }
}
