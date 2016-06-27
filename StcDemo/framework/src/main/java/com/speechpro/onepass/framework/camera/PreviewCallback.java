package com.speechpro.onepass.framework.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.Log;
import com.speechpro.onepass.framework.util.Util;
import com.speechpro.onepass.framework.view.BorderView;

import java.io.IOException;
import static com.speechpro.onepass.framework.util.BitmapUtil.createBitmap;
import static com.speechpro.onepass.framework.util.BitmapUtil.createJpeg;


/**
 * @author volobuev
 * @since 25.03.16
 */
public class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = "PreviewCallback";

    private boolean isAttached   = false;
    private boolean isPhotoTaken = false;

    private Bitmap bitmap;
    private byte[] imageJpeg;

    private Integer successFaceCount = 0;
//    private ProcessPreviewDataTask processPreviewDataTask;
//    private ProcessPhotoTask processPhotoTask;
    private BorderView             borderView;
//    private VisionView             visionView;
    private Context                context;
    private int                    rotation;
    private int                    width;
    private int                    height;

    public PreviewCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        bitmap = createBitmap(data, width, height, rotation);
        takeImage();

        if (!isAttached || isPhotoTaken) {
            return;
        }

        //        if (processPhotoTask == null || processPhotoTask.getState().equals(Thread.State.TERMINATED)) {
        //            processPhotoTask = new ProcessPhotoTask();
        //            processPhotoTask.start();
        //        }

//        if (processPreviewDataTask == null || processPreviewDataTask.getStatus().equals(FINISHED)) {
//            processPreviewDataTask = new ProcessPreviewDataTask();
//            processPreviewDataTask.executeOnExecutor(Executors.newSingleThreadExecutor());
//        }

//        if (visionView != null && borderView.successFrames() <= successFaceCount && !isPhotoTaken) {
//            visionView.processVision();
//            isPhotoTaken = true;
//        }
    }

    public void setBorderView(BorderView borderView) {
        this.borderView = borderView;
    }

//    public void setVisionView(VisionView visionView) {
//        this.visionView = visionView;
//    }

    public void setParameters(Camera.Parameters parameters) {
        Camera.Size       size = parameters.getPreviewSize();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        rotation = info.orientation;
        width = size.width;
        height = size.height;
    }

    public void onRedBorder() {
        borderView.onRedBorder();
    }

    public void onAttach() {
//        faceDetector = new SafeFaceDetector(context);
        isAttached = true;
        isPhotoTaken = false;
//        processPreviewDataTask = null;
        successFaceCount = 0;
    }

    public void onDetach() {
        isAttached = false;
    }

    public byte[] getImage() {
        Util.logFaces(imageJpeg);
        return imageJpeg;
    }

    private byte[] takeImage() {
        try {
            imageJpeg = createJpeg(bitmap, 80);
            Log.d(TAG, "Photo is taken");
            return imageJpeg;
        } catch (IOException err) {
            Log.e(TAG, "Photo taking is failed: ", err);
        }
        return null;
    }

//    private class ProcessPreviewDataTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            if (bitmap != null) {
//                byte[] img = getImage();
//                Util.logFaces(img);
//                long    millisStart = System.currentTimeMillis();
//                boolean isConform   = faceDetector.isFaceConform(bitmap);
//                bitmap.recycle();
//                Log.d(TAG, "Conforming is " + (System.currentTimeMillis() - millisStart));
//                if (isConform) {
//                    successFaceCount++;
//                    borderView.onGreenBorder();
//                } else {
//                    successFaceCount = 0;
//                    borderView.onNormalBorder();
//                }
//            }
//            return null;
//        }
//    }

    //    private class ProcessPhotoTask extends Thread {
    //
    //        @Override
    //        public void run() {
    //            if (imageSource != null) {
    //                byte[] img = takeImage();
    //                Util.logFaces(img);
    //
    //                long millisStart = System.currentTimeMillis();
    //                boolean isConform = faceDetector.isFaceConform(img);
    //                LOG.debug("Conforming is " + (System.currentTimeMillis() - millisStart));
    //
    //                synchronized (successFaceCount) {
    //                    if (isConform) {
    //                        successFaceCount++;
    //                        borderView.onGreenBorder();
    //                    } else {
    //                        successFaceCount = 0;
    //                        borderView.onNormalBorder();
    //                    }
    //                }
    //            }
    //        }
    //    }

}
