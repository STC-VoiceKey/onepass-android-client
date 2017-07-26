/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.speechpro.onepass.framework.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.hardware.Camera;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.listeners.CameraCallbackListener;
import com.speechpro.onepass.framework.ui.listeners.EnrollCameraCallbackListener;

import java.io.IOException;
import java.lang.reflect.Field;

public class CameraSourcePreview extends ViewGroup
        implements CameraSource.ShutterCallback, CameraSource.PictureCallback {

    private static final String TAG = CameraSourcePreview.class.getSimpleName();

    private static final int RC_HANDLE_GMS = 9001;

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;

    private GraphicOverlayView mOverlay;

    private CameraCallbackListener mListener;

    private int mSensorOrientation;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);

        createCameraSource();
    }

    private void start(CameraSource cameraSource) throws IOException {

        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    private void start(CameraSource cameraSource, GraphicOverlayView overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes) {
        Log.d(TAG, "PICTURE TAKEN FROM CAMERA");
        if (mListener != null && mListener instanceof EnrollCameraCallbackListener) {
            ((EnrollCameraCallbackListener) mListener).onPictureCaptured(bytes, mSensorOrientation);
        }
    }

    @Override
    public void onShutter() {

    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            try {
                mCameraSource.start(mSurfaceView.getHolder());
                Field[] declaredFields = CameraSource.class.getDeclaredFields();

                for (Field field : declaredFields) {
                    if (field.getType() == Camera.class) {
                        field.setAccessible(true);
                        try {
                            Camera camera = (Camera) field.get(mCameraSource);
                            if (camera != null) {
                                Camera.Parameters params = camera.getParameters();
//                                    camera.setDisplayOrientation(0);

                                Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
                                int camerasCounter = Camera.getNumberOfCameras();
                                int cameraId;
                                if (camerasCounter > 1) {
                                    cameraId = 1;
                                } else if (camerasCounter == 1) {
                                    cameraId = 0;
                                } else {
                                    return;
                                }
                                Camera.getCameraInfo(cameraId, mCameraInfo);

//                                    Log.d(TAG, "mSensorOrientation: " + mCameraInfo.orientation);
//
//                                    int degrees = 0;
//                                    if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                                        mSensorOrientation = (mCameraInfo.orientation + degrees) % 360;
//                                        mSensorOrientation = (360 - mSensorOrientation) % 360;  // compensate the mirror
//                                    } else {  // back-facing
//                                        mSensorOrientation = (mCameraInfo.orientation - degrees + 360) % 360;
//                                    }

                                mSensorOrientation = 0;
                            }

                        } catch (IllegalAccessException | RuntimeException e) {
                            e.getMessage();
                        }

                        break;
                    }
                }

                mSurfaceAvailable = true;

            } catch (IOException | RuntimeException e) {
                Log.e("CAMERA SOURCE", e.getMessage());
                e.printStackTrace();
                Snackbar.make(mSurfaceView, R.string.camera_locked,
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int previewWidth = 320;
        int previewHeight = 240;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                previewWidth = size.getWidth();
                previewHeight = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = tmp;
        }

        final int viewWidth = right - left;
        final int viewHeight = bottom - top;

        int childWidth;
        int childHeight;
        int childXOffset = 0;
        int childYOffset = 0;
        float widthRatio = (float) viewWidth / (float) previewWidth;
        float heightRatio = (float) viewHeight / (float) previewHeight;

        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
        // it is usually necessary to slightly oversize the child and to crop off portions along one
        // of the dimensions.  We scale up based on the dimension requiring the most correction, and
        // compute a crop offset for the other dimension.
        if (widthRatio > heightRatio) {
            childWidth = viewWidth;
            childHeight = (int) ((float) previewHeight * widthRatio);
            childYOffset = (childHeight - viewHeight) / 2;
        } else {
            childWidth = (int) ((float) previewWidth * heightRatio);
            childHeight = viewHeight;
            childXOffset = (childWidth - viewWidth) / 2;
        }

        for (int i = 0; i < getChildCount(); ++i) {
            // One dimension will be cropped.  We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            getChildAt(i).layout(
                    -1 * childXOffset, -1 * childYOffset,
                    childWidth - childXOffset, childHeight - childYOffset);
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }

    public synchronized void setListener(CameraCallbackListener listener) {
        this.mListener = listener;
    }

    public synchronized void removeListener() {
        this.mListener = null;
    }

    public void captureImage() {
        mCameraSource.takePicture(this, this);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    public void createCameraSource() {

        Context context = mContext.getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(60.0f)
                .build();
    }

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> implements FaceCallback {

        private static final float EYE_CLOSED_THRESHOLD = 0.4f;

        private GraphicOverlayView mOverlay;
        private FaceGraphic mFaceGraphic;

        private PointF mPreviousPosition;

        // Similarly, keep track of the previous eye open state so that it can be reused for
        // intermediate frames which lack eye landmarks and corresponding eye state.
        private boolean mPreviousIsLeftOpen = true;
        private boolean mPreviousIsRightOpen = true;

        GraphicFaceTracker(GraphicOverlayView overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay, GraphicFaceTracker.this);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);

            if (mListener != null) {
                CameraCallbackListener listener = mListener;
                listener.onFaceDetected();
            }
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

            if (mListener == null) return;

            PointF currentPosition = face.getPosition();
            if (mPreviousPosition != null && currentPosition != null) {
                double offset = Math.sqrt(Math.pow(currentPosition.x - mPreviousPosition.x, 2)
                        + Math.pow(currentPosition.y - mPreviousPosition.y, 2));
                if (offset > 5) {
                    mListener.onShakingCamera(true);
                } else {
                    mListener.onShakingCamera(false);
                }
            }

            mPreviousPosition = currentPosition;

            float leftOpenScore = face.getIsLeftEyeOpenProbability();
            boolean isLeftOpen;
            if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
                isLeftOpen = mPreviousIsLeftOpen;
            } else {
                isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
                mPreviousIsLeftOpen = isLeftOpen;
            }

            float rightOpenScore = face.getIsRightEyeOpenProbability();
            boolean isRightOpen;
            if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
                isRightOpen = mPreviousIsRightOpen;
            } else {
                isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
                mPreviousIsRightOpen = isRightOpen;
            }

            SparseArray<Face> items = detectionResults.getDetectedItems();

            CameraCallbackListener listener = mListener;
            listener.onEyesOpen(isLeftOpen && isRightOpen && leftOpenScore != -1 && rightOpenScore != -1);
            listener.onFaceCount(items.size());
            listener.onFaceDetected();
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);

            if (mListener != null) {
                mListener.onFaceLost();
            }
        }

        @Override
        public void onInArea(boolean isInArea) {
            if (mListener != null) {
                mListener.onFaceInCenter(isInArea);
            }
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    public void startCameraSource(GraphicOverlayView graphicOverlay) {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                mContext.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog((Activity) mContext, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                start(mCameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
}
