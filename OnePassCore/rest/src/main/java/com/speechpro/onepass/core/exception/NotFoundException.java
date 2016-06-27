package com.speechpro.onepass.core.exception;

/**
 * Occurs when sending data in RESTful methods is wrong.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public class NotFoundException extends RestException {
    public NotFoundException(final String message) {
        super(message);
    }
}
