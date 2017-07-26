package com.speechpro.onepass.framework.media;

/**
 * @author volobuev
 * @since 10.11.16
 */
public interface AudioListener {
    void start();
    void stop(byte[] result);
    void onProcess(short amplitude);
}
