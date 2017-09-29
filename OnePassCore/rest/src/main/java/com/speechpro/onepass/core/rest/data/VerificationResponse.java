package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.speechpro.onepass.core.exception.NotFoundException;

/**
 * Describes verification result.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class VerificationResponse {

    @JsonProperty("status")
    public String result;
    @JsonProperty("message")
    public String message;

    /**
     * Constructor.
     *
     * @param result  result from server
     * @param message the message containing an result explanation
     */
    @JsonCreator
    public VerificationResponse(@JsonProperty("status") String result, @JsonProperty("message") String message) {
        this.result = result;
        this.message = message;
    }

    public boolean compileResult() throws NotFoundException {
        if (result.trim().equalsIgnoreCase("success")) {
            return true;
        } else if (result.trim().equalsIgnoreCase("failure")) {
            return false;
        } else {
            throw new NotFoundException("Failed to parse message {status:" + result + ", message:" + message + "}.", null);
        }

    }
}
