package com.speechpro.onepass.framework.model.data;

import java.util.Arrays;

/**
 * @author volobuev
 * @since 17.02.2016
 */
public class FaceSample {
    private final byte[] faceSample;

    public FaceSample(byte[] faceSample) {
        this.faceSample = faceSample;
    }

    public byte[] getFaceSample() {
        return faceSample;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaceSample)) return false;

        FaceSample that = (FaceSample) o;

        return Arrays.equals(faceSample, that.faceSample);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(faceSample);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FaceSample{");
        sb.append("faceSample=");
        if (faceSample == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < faceSample.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(faceSample[i]);
            sb.append(']');
        }
        sb.append('}');
        return sb.toString();
    }
}
