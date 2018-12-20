package com.speechpro.onepass.core.sessions;

/**
 * Created by alexander on 11.09.17.
 */

public class ScoreVerify {

    private float face;
    private float dynamicVoice;
    private float staticVoice;
    private float fused;
    private boolean isAlive;

    public ScoreVerify(float face, float dynamicVoice, float staticVoice, float fused, boolean isAlive) {
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
