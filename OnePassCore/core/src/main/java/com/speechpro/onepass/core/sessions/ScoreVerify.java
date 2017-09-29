package com.speechpro.onepass.core.sessions;

/**
 * Created by alexander on 11.09.17.
 */

public class ScoreVerify {

    private float face;
    private float dynamicVoice;
    private float staticVoice;
    private float fused;
    private float liveness;

    public ScoreVerify(float face, float dynamicVoice, float staticVoice, float fused, float liveness) {
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
