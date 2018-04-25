package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Alexander Grigal on 08.02.18.
 */

public class ErrorResponse {

    @JsonProperty(value = "reason", required = true)
    public final String reason;
    @JsonProperty(value = "message", required = true)
    public final String message;

    @JsonCreator
    public ErrorResponse(@JsonProperty(value = "reason", required = true) String reason,
                         @JsonProperty(value = "message", required = true) String message) {
        this.reason = reason;
        this.message = message;
    }
}

