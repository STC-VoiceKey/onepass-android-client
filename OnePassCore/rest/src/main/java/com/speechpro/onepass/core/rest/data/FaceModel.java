package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FaceModel representation.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class FaceModel {

    @JsonProperty(value = "model", required = true)
    public final Data model;

    /**
     * Constructor.
     *
     * @param model contains model data
     */
    @JsonCreator
    public FaceModel(@JsonProperty(value = "model", required = true) Data model) {
        this.model = model;
    }

}
