package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by alexander on 07.08.17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class VerificationScoresResponse {

    @JsonProperty("face")
    public float face;
    @JsonProperty("dynamic_voice")
    public float dynamicVoice;
    @JsonProperty("static_voice")
    public float staticVoice;
    @JsonProperty("fused")
    public float fused;
    @JsonProperty("is_alive")
    public boolean isAlive;

    @JsonCreator
    public VerificationScoresResponse(@JsonProperty("face") float face,
                                      @JsonProperty("dynamic_voice") float dynamicVoice,
                                      @JsonProperty("static_voice") float staticVoice,
                                      @JsonProperty("fused") float fused,
                                      @JsonProperty("is_alive") boolean isAlive) {
        this.face = face;
        this.dynamicVoice = dynamicVoice;
        this.staticVoice = staticVoice;
        this.fused = fused;
        this.isAlive = isAlive;
    }

    public float getFace() {
        return face;
    }

    public float getDynamicVoice() {
        return dynamicVoice;
    }

    public float getStaticVoice() {
        return staticVoice;
    }

    public float getFused() {
        return fused;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
