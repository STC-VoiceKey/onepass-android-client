package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Contains data for create person request.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class CreatePersonRequest {

    @JsonProperty(value = "personId", required = true)
    public final String personId;

    /**
     * Constructor.
     *
     * @param personId person id for creating
     */
    @JsonCreator
    public CreatePersonRequest(@JsonProperty(value = "personId", required = true) String personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreatePersonRequest)) {
            return false;
        }
        CreatePersonRequest that = (CreatePersonRequest) o;
        return Objects.equal(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(personId);
    }
}
