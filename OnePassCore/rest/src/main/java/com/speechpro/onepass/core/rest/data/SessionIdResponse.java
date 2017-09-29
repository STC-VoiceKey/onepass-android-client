package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
        fieldVisibility = JsonAutoDetect.Visibility.NONE
)
public class SessionIdResponse {
    @JsonProperty("sessionId")
    public String sessionId;

    @JsonCreator
    public SessionIdResponse(@JsonProperty("sessionId") String sessionId) {
        this.sessionId = sessionId;
    }
}
