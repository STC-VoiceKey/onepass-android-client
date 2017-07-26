package com.speechpro.onepass.framework.ui.listeners;

/**
 * Additional verification camera callback actions
 *
 * @author volobuev
 * @since 15.08.16
 */
public interface VerifyCameraCallbackListener extends CameraCallbackListener {

    void onFaceDetectionNotSupported();

    void onVideoCaptured(String path);
}
