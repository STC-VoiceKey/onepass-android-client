package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FaceSample representation.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class FaceSample {

    @JsonProperty(value = "sample", required = true)
    public final Data sample;

    /**
     * Constructor.
     *
     * @param sample contains sample data
     */
    @JsonCreator
    public FaceSample(@JsonProperty(value = "sample", required = true) Data sample) {
        this.sample = sample;
    }

    public Data getSample() {
        return sample;
    }
}
