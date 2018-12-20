package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * VoiceSample representation.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class VoiceSample {

    @JsonProperty(value = "data", required = true)
    public final byte[] data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "password")
    public String password;
    @JsonProperty(value = "sampling_rate", required = true)
    public final int    samplingRate;

    /**
     * Constructor.
     *
     * @param data         data
     * @param password     passphrase
     * @param samplingRate sampling rate
     */
    @JsonCreator
    public VoiceSample(@JsonProperty(value = "data", required = true) byte[] data,
                       @JsonProperty(value = "password", required = true) String password,
                       @JsonProperty(value = "sampling_rate", required = true) int samplingRate) {
        this.data = data;
        this.password = password;
        this.samplingRate = samplingRate;
    }

    /**
     * Constructor.
     *
     * @param data         data
     * @param samplingRate sampling rate
     */
    @JsonCreator
    public VoiceSample(@JsonProperty(value = "data", required = true) byte[] data,
                       @JsonProperty(value = "sampling_rate", required = true) int samplingRate) {
        this.data = data;
        this.samplingRate = samplingRate;
    }

}
