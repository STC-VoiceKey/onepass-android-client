package com.speechpro.onepass.framework.model.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author volobuev
 * @since 19.02.2016
 */
public class Video {
    private byte[] video;
    private final String passphrase;

    public Video(byte[] video, String passphrase) {
        this.video = video;
        this.passphrase = passphrase;
    }

    public byte[] getVideo() {
        return video;
    }

    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Video)) {
            return false;
        }
        Video video1 = (Video) o;
        return Objects.equal(video, video1.video) && Objects.equal(passphrase, video1.passphrase);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(video, passphrase);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("video", video).add("passphrase", passphrase).toString();
    }
}
