package com.speechpro.onepass.framework.ui.listeners;

/**
 * @author volobuev
 * @since 15.08.16
 */
public interface EnrollCameraCallbackListener extends CameraCallbackListener {
    /**
     * Picture preview captured
     *
     * @param picture picture byte array
     */
    void onPictureCaptured(byte[] picture, int degrees);

}
