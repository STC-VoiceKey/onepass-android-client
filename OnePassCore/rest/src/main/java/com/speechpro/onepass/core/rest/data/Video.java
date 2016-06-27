package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Video representation.
 *
 * @author volobuev
 * @since 19.01.2016
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class Video {
    @JsonProperty(value = "data", required = true)
    public final byte[] data;
    @JsonProperty(value = "password", required = true)
    public final String password;

    /**
     * Constructor.
     *
     * @param data     video data
     * @param password verification passphrase
     */
    @JsonCreator
    public Video(@JsonProperty(value = "data", required = true) byte[] data,
                 @JsonProperty(value = "password", required = true) String password) {
        this.data = data;
        this.password = password;
    }
}
