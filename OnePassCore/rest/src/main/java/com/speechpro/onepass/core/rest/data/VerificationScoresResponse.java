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
    @JsonProperty("dynamicVoice")
    public float dynamicVoice;
    @JsonProperty("staticVoice")
    public float staticVoice;
    @JsonProperty("fused")
    public float fused;
    @JsonProperty("liveness")
    public float liveness;

    @JsonCreator
    public VerificationScoresResponse(@JsonProperty("face") float face,
                                      @JsonProperty("dynamicVoice") float dynamicVoice,
                                      @JsonProperty("staticVoice") float staticVoice,
                                      @JsonProperty("fused") float fused,
                                      @JsonProperty("liveness") float liveness) {
        this.face = face;
        this.dynamicVoice = dynamicVoice;
        this.staticVoice = staticVoice;
        this.fused = fused;
        this.liveness = liveness;
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

    public float getLiveness() {
        return liveness;
    }
}
