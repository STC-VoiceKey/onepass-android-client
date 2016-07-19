package com.speechpro.onepass.core.exception;


/**
 * Throws when something went wrong in RESTful interaction.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public class RestException extends CoreException {

    public String reason;

    protected RestException(final String message, final String reason) {
        super(message);
        this.reason = reason;
    }

}
