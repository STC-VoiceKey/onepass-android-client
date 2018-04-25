package com.speechpro.onepass.framework.ui.view.camera.callbacks;

/**
 * Callback interface used to notify on completion of camera auto focus.
 */
public interface AutoFocusCallback {
    /**
     * Called when the camera auto focus completes.  If the camera
     * does not support auto-focus and autoFocus is called,
     * onAutoFocus will be called immediately with a fake value of
     * <code>success</code> set to <code>true</code>.
     * <p/>
     * The auto-focus routine does not lock auto-exposure and auto-white
     * balance after it completes.
     *
     * @param success true if focus was successful, false if otherwise
     */
    void onAutoFocus(boolean success);
}
