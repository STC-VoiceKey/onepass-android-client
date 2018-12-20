package com.speechpro.onepass.framework.model.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author volobuev
 * @since 17.02.2016
 */
public class VoiceSample {

    private final byte[] voiceSample;
    private String passphrase;
    private final int    samplingRate;

    public VoiceSample(byte[] voiceSample, String passphrase, int samplingRate) {
        this.voiceSample = voiceSample;
        this.passphrase = passphrase;
        this.samplingRate = samplingRate;
    }

    public VoiceSample(byte[] voiceSample, int samplingRate) {
        this.voiceSample = voiceSample;
        this.samplingRate = samplingRate;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public byte[] getVoiceSample() {
        return voiceSample;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceSample that = (VoiceSample) o;
        return samplingRate == that.samplingRate &&
                Objects.equal(voiceSample, that.voiceSample) &&
                Objects.equal(passphrase, that.passphrase);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(voiceSample, passphrase, samplingRate);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("voiceSample", voiceSample)
                          .add("passphrase", passphrase)
                          .add("samplingRate", samplingRate)
                          .toString();
    }
}
