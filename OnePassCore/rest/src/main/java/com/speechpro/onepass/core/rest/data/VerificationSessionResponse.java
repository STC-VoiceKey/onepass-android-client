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
    @JsonProperty("transaction_id")
    public String transactionId;

    /**
     * Constructor.
     *
     * @param password       password which was said
     * @param transactionId  verification transaction id
     */
    @JsonCreator
    public VerificationSessionResponse(@JsonProperty("password") String password,
                                       @JsonProperty("transaction_id") String transactionId) {
        this.password = password;
        this.transactionId = transactionId;
    }


    public String getPassword() {
        return password;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
