package com.speechpro.onepass.core.exception;

//TODO: Add comment

/**
 * @author volobuev
 * @since 28.09.2015.
 */
public class BadRequestException extends RestException {

    public BadRequestException(String message, String reason) {
        super(message, reason);
    }

}
