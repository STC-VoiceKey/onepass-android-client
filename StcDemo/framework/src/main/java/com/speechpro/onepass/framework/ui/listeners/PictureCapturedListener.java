package com.speechpro.onepass.framework.ui.listeners;

/**
 * Listener interface used to supply image data from a photo capture.
 */
public interface PictureCapturedListener {
    /**
     * Picture preview captured
     *
     * @param picture byte array
     * @param degrees angle
     */
    void onPictureCaptured(byte[] picture, int degrees);

}
