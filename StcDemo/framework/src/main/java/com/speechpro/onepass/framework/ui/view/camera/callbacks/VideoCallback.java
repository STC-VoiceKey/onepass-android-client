package com.speechpro.onepass.framework.ui.view.camera.callbacks;

/**
 * Callback interface used to indicate when video Recording Started or Stopped.
 */
public interface VideoCallback {
    //Called when Video Recording started.
    void onVideoStart();
    //Called when Video Recording stopped.
    void onVideoStop(String videoFile);
    //Called when error ocurred while recording video.
    void onVideoError(String error);

}
