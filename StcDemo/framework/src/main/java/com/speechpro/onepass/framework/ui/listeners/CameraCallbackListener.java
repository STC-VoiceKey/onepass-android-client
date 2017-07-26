package com.speechpro.onepass.framework.ui.listeners;

/**
 * @author volobuev
 * @since 12.08.16
 */
public interface CameraCallbackListener {

    void onCameraReady(int width, int height);

    void onCameraError(int errorCode);

    void onCameraClose();

    void onFaceDetected();

    void onFaceLost();

    void onEyesOpen(boolean isOpen);

    void onFaceCount(int count);

    void onFaceInCenter(boolean isInCenter);

    void onShakingCamera(boolean isShaking);

}
