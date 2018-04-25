package com.speechpro.onepass.core.exception;

/**
 * Created by Alexander Grigal on 20.02.18.
 */

public class RetrofitException extends RestException {

    public RetrofitException(String message, String reason) {
        super(message, reason);
    }

}
