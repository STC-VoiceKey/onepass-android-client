package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Server response after verification.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class VerificationSessionResponse {

    @JsonProperty("password")
    public String password;
    @JsonProperty("verificationId")
    public String verificationId;

    /**
     * Constructor.
     *
     * @param password       password which was said
     * @param verificationId verification session id
     */
    @JsonCreator
    public VerificationSessionResponse(@JsonProperty("password") String password,
                                       @JsonProperty("verificationId") String verificationId) {
        this.password = password;
        this.verificationId = verificationId;
    }


    public String getPassword() {
        return password;
    }

    public String getVerificationId() {
        return verificationId;
    }
}
