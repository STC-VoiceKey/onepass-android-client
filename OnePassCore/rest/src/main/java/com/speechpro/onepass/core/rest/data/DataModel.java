package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class DataModel {

    @JsonProperty("id")
    public final Long   id;
    @JsonProperty("type")
    public final String type;
    @JsonProperty("samples_count")
    public final int    samplesCount;

    @JsonCreator
    public DataModel(@JsonProperty("id") Long id,
                     @JsonProperty("type") String type,
                     @JsonProperty("samples_count") int samplesCount) {
        this.id = id;
        this.type = type;
        this.samplesCount = samplesCount;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .add("type", type)
                          .add("samples_count", samplesCount)
                          .toString();
    }
}
