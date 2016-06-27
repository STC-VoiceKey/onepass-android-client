package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Set;

/**
 * Describes person representation. It has unique id and data models.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class Person {

    @JsonProperty("id")
    public final String         id;
    @JsonProperty("models")
    public final Set<DataModel> models;
    @JsonProperty("isFullEnroll")
    public final boolean        isFullEnroll;

    @JsonCreator
    public Person(@JsonProperty("id") String id,
                  @JsonProperty("models") Set<DataModel> models,
                  @JsonProperty("isFullEnroll") boolean isFullEnroll) {
        this.id = id;
        this.models = models;
        this.isFullEnroll = isFullEnroll;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).
                add("id", id).
                                  add("models", models).
                                  add("isFullEnroll", isFullEnroll).
                                  toString();
    }
}
