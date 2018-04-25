package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by alexander on 14.08.17.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class VoiceFile {

    @JsonProperty(value = "data", required = true)
    public final byte[] data;
    @JsonProperty(value = "password", required = true)
    public final String password;
    @JsonProperty(value = "channel", required = true)
    public final int    channel;

    /**
     * Constructor.
     *
     * @param data         sample data
     * @param password     passphrase
     * @param channel      chanel
     */
    @JsonCreator
    public VoiceFile(@JsonProperty(value = "data", required = true) byte[] data,
                     @JsonProperty(value = "password", required = true) String password,
                     @JsonProperty(value = "channel", required = true) int channel) {

        this.data = data;
        this.password = password;
        this.channel = channel;
    }

}
