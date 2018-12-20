package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Created by alexander on 28.07.17.
 */

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonAutoDetect(
        fieldVisibility = Visibility.NONE
)
public class RegistationSessionResponse {
    @JsonProperty("transaction_id")
    public String transactionId;

    @JsonCreator
    public RegistationSessionResponse(@JsonProperty("transaction_id") String transactionId) {
        this.transactionId = transactionId;
    }
}
