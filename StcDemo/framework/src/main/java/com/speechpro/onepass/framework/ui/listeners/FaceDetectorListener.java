package com.speechpro.onepass.framework.ui.listeners;

/**
 * Listener interface used to indicate when face detecting.
 */
public interface FaceDetectorListener {

    void onFaceDetected();

    void onFaceLost();

    void onEyesOpen(boolean isOpen);

    void onFaceCount(int count);

    void onFaceInCenter(boolean isInCenter);

    void onShakingCamera(boolean hasShaking);

}
