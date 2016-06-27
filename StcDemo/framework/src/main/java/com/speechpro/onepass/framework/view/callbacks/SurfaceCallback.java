package com.speechpro.onepass.framework.view.callbacks;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import com.speechpro.onepass.framework.camera.PreviewCallback;

import java.io.IOException;

/**
 * @author volobuev
 * @since 12.05.16
 */
public class SurfaceCallback implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceCallback";

    private final Camera          camera;
    private final PreviewCallback previewCallback;
    private final SurfaceView     preview;

    public SurfaceCallback(Camera camera, PreviewCallback previewCallback, SurfaceView preview) {
        this.camera = camera;
        this.previewCallback = previewCallback;
        this.preview = preview;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.e(TAG, "Camera cannot set preview display: ", e);
            e.printStackTrace();
        }
        camera.setPreviewCallback(previewCallback);

        Camera.Size            previewSize          = camera.getParameters().getPreviewSize();
        float                  aspect               = (float) previewSize.width / previewSize.height;
        int                    previewSurfaceHeight = preview.getHeight();
        ViewGroup.LayoutParams lp                   = preview.getLayoutParams();

        lp.height = previewSurfaceHeight;
        lp.width = (int) (previewSurfaceHeight / aspect);

        preview.setLayoutParams(lp);
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
