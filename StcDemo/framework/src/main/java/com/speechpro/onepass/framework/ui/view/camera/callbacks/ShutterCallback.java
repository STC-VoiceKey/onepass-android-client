package com.speechpro.onepass.framework.ui.view.camera.callbacks;

/**
 * Callback interface used to signal the moment of actual image capture.
 */
public interface ShutterCallback {
    /**
     * Called as near as possible to the moment when a photo is captured from the sensor. This
     * is a good opportunity to play a shutter sound or give other feedback of camera operation.
     * This may be some time after the photo was triggered, but some time before the actual data
     * is available.
     */
    void onShutter();
}
