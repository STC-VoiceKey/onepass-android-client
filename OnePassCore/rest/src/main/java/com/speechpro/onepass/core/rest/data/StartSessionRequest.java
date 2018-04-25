package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.common.base.Objects;
/**
 * Created by alexander on 28.07.17.
 */

@JsonAutoDetect(
        fieldVisibility = Visibility.NONE
)
public class StartSessionRequest {
    @JsonProperty(
            value = "username",
            required = true
    )
    public final String username;
    @JsonProperty(
            value = "password",
            required = true
    )
    public final String password;
    @JsonProperty(
            value = "domainId",
            required = true
    )
    public final int domainId;

    @JsonCreator
    public StartSessionRequest(@JsonProperty(value = "username",required = true) String username,
                               @JsonProperty(value = "password",required = true) String password,
                               @JsonProperty(value = "domainId",required = true) int domainId) {
        this.username = username;
        this.password = password;
        this.domainId = domainId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            StartSessionRequest that = (StartSessionRequest)o;
            return this.domainId == that.domainId
                    && Objects.equal(this.username, that.username)
                    && Objects.equal(this.password, that.password);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.username, this.password, this.domainId});
    }
}
