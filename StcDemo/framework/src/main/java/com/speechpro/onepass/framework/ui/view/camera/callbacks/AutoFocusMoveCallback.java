package com.speechpro.onepass.framework.ui.view.camera.callbacks;

import android.hardware.Camera;

/**
 * Callback interface used to notify on auto focus start and stop.
 * <p/>
 * <p>This is only supported in continuous autofocus modes -- {@link
 * Camera.Parameters#FOCUS_MODE_CONTINUOUS_VIDEO} and {@link
 * Camera.Parameters#FOCUS_MODE_CONTINUOUS_PICTURE}. Applications can show
 * autofocus animation based on this.</p>
 */
public interface AutoFocusMoveCallback {
    /**
     * Called when the camera auto focus starts or stops.
     *
     * @param start true if focus starts to move, false if focus stops to move
     */
    void onAutoFocusMoving(boolean start);
}
