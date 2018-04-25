package com.speechpro.onepass.framework.ui.view.camera.callbacks;

import android.graphics.Bitmap;

/**
 * Callback interface used to supply image data from a photo capture.
 */
public interface PictureCallback {

    void onPictureTaken(Bitmap picture);

}