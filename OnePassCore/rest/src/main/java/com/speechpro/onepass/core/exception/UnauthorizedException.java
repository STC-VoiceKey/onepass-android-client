package com.speechpro.onepass.core.exception;

/**
 * Created by Alexander Grigal on 16.03.18.
 */

public class UnauthorizedException extends RestException {

    public UnauthorizedException(String message, String reason) {
        super(message, reason);
    }

}
