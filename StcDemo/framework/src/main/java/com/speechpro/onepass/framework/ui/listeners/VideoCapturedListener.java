package com.speechpro.onepass.framework.ui.listeners;

/**
 * Listener interface used to indicate when video captured.
 */
public interface VideoCapturedListener {
    /**
     * Video captured
     *
     * @param path full patch
     */
    void onVideoCaptured(String path);

}
