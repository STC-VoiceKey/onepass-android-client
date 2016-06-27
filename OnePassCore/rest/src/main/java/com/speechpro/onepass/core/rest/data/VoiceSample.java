package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
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
    @JsonProperty(value = "password", required = true)
    public final String password;
    @JsonProperty(value = "gender", required = false)
    public final int    gender;
    @JsonProperty(value = "sampling_rate", required = true)
    public final int    samplingRate;

    /**
     * Constructor.
     *
     * @param data         sample data
     * @param password     passphrase
     * @param gender       person gender
     * @param samplingRate sampling rate
     */
    @JsonCreator
    public VoiceSample(@JsonProperty(value = "data", required = true) byte[] data,
                       @JsonProperty(value = "password", required = true) String password,
                       @JsonProperty(value = "gender", required = false) int gender,
                       @JsonProperty(value = "sampling_rate", required = true) int samplingRate) {
        this.data = data;
        this.password = password;
        this.gender = gender;
        this.samplingRate = samplingRate;
    }

    public VoiceSample(byte[] data, String password, int samplingRate) {
        this(data, password, 0, samplingRate);
    }

}
