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
package com.speechpro.onepass.framework.ui.view.camera;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.speechpro.onepass.framework.ui.listeners.FaceDetectorListener;
import com.speechpro.onepass.framework.ui.listeners.PictureCapturedListener;
import com.speechpro.onepass.framework.ui.listeners.VideoCapturedListener;
import com.speechpro.onepass.framework.ui.view.AutoFitTextureView;
import com.speechpro.onepass.framework.ui.view.FaceCallback;
import com.speechpro.onepass.framework.ui.view.FaceGraphic;
import com.speechpro.onepass.framework.ui.view.GraphicOverlayView;
import com.speechpro.onepass.framework.ui.view.camera.callbacks.PictureCallback;
import com.speechpro.onepass.framework.ui.view.camera.callbacks.ShutterCallback;
import com.speechpro.onepass.framework.ui.view.camera.callbacks.VideoCallback;
import com.speechpro.onepass.framework.util.LogUtils;
import com.speechpro.onepass.framework.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;


public class CameraSourcePreview extends ViewGroup
        implements ShutterCallback, PictureCallback, VideoCallback {

    private static final String TAG = CameraSourcePreview.class.getSimpleName();

    private final Context mContext;

    private SurfaceView mSurfaceView;
    private AutoFitTextureView mAutoFitTextureView;
    private boolean mHasBorder;

    private boolean usingCameraOne;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private boolean viewAdded = false;

    private CameraSource mCameraSource;
    private Camera2Source mCamera2Source;
    private boolean mIsRecordingVideo;
    private String mVideoPath;

    private GraphicOverlayView mOverlay;
    private FaceDetectorListener mFaceDetectorListener;
    private PictureCapturedListener mPictureCapturedListener;
    private VideoCapturedListener mVideoCapturedListener;

    private int screenWidth;
    private int screenHeight;
    private int screenRotation;

    // Must be careful using this variable.
    // Any attempt to start camera2 on API < 21 will crash.
    private boolean useCamera2;

    private int mSensorOrientation;

    private static final int RC_HANDLE_GMS = 9001;

    public CameraSourcePreview(Context context) {
        super(context);
        mContext = context;
        screenHeight = Utils.getScreenHeight(context);
        screenWidth = Utils.getScreenWidth(context);
        screenRotation = Utils.getScreenRotation(context);
        mStartRequested = false;
        mSurfaceAvailable = false;
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(mSurfaceViewListener);
        mAutoFitTextureView = new AutoFitTextureView(context);
        mAutoFitTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            useCamera2 = true;
        }

    }

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        screenHeight = Utils.getScreenHeight(context);
        screenWidth = Utils.getScreenWidth(context);
        screenRotation = Utils.getScreenRotation(context);
        mStartRequested = false;
        mSurfaceAvailable = false;
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(mSurfaceViewListener);
        mAutoFitTextureView = new AutoFitTextureView(context);
        mAutoFitTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            useCamera2 = true;
        }

    }

    private void start(CameraSource cameraSource, GraphicOverlayView overlay) throws IOException {
        usingCameraOne = true;
        mOverlay = overlay;
        start(cameraSource);
    }

    private void start(Camera2Source camera2Source, GraphicOverlayView overlay) throws IOException {
        usingCameraOne = false;
        mOverlay = overlay;
        start(camera2Source);
    }

    private void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }
        mCameraSource = cameraSource;
        if (mCameraSource != null) {
            mStartRequested = true;
            if (!viewAdded) {
                addView(mSurfaceView);
                viewAdded = true;
            }
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }
    }

    private void start(Camera2Source camera2Source) throws IOException {
        if (camera2Source == null) {
            stop();
        }
        mCamera2Source = camera2Source;
        if (mCamera2Source != null) {
            mStartRequested = true;
            if (!viewAdded) {
                addView(mAutoFitTextureView);
                viewAdded = true;
            }
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }
    }

    public void stop() {
        mStartRequested = false;
        if (usingCameraOne) {
            if (mCameraSource != null) {
                mCameraSource.stop();
            }
        } else {
            if (mCamera2Source != null) {
                mCamera2Source.stop();
            }
        }
    }

    public void release() {
        if (usingCameraOne) {
            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }
        } else {
            if (mCamera2Source != null) {
                mCamera2Source.release();
                mCamera2Source = null;
            }
        }
    }

    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            try {
                if (usingCameraOne) {
                    mCameraSource.start(mSurfaceView.getHolder());
                    if (mOverlay != null) {
                        Size size = mCameraSource.getPreviewSize();
                        if (size != null) {
                            int min = Math.min(size.getWidth(), size.getHeight());
                            int max = Math.max(size.getWidth(), size.getHeight());
                            // For graphic overlay, the preview size was reduced to quarter
                            // In order to prevent CPU overload
                            mOverlay.setCameraInfo(min / 4, max / 4, mCameraSource.getCameraFacing());
                            mOverlay.clear();
                        } else {
                            stop();
                        }
                    }
                    mStartRequested = false;
                } else {
                    mCamera2Source.start(mAutoFitTextureView, screenRotation);
                    if (mOverlay != null) {
                        Size size = mCamera2Source.getPreviewSize();
                        if (size != null) {
                            int min = Math.min(size.getWidth(), size.getHeight());
                            int max = Math.max(size.getWidth(), size.getHeight());
                            // For graphic overlay, the preview size was reduced to quarter
                            // In order to prevent CPU overload
                            mOverlay.setCameraInfo(min / 4, max / 4, mCamera2Source.getCameraFacing());
                            mOverlay.clear();
                        } else {
                            stop();
                        }
                    }
                    mStartRequested = false;
                }
            } catch (SecurityException e) {
                Log.d(TAG, "SECURITY EXCEPTION: " + e);
            }
        }
    }

    private final SurfaceHolder.Callback mSurfaceViewListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            mOverlay.bringToFront();
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
    };

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            mSurfaceAvailable = true;
            mOverlay.bringToFront();
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            mSurfaceAvailable = false;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 480;
        int height = 720;
        if (usingCameraOne) {
            if (mCameraSource != null) {
                Size size = mCameraSource.getPreviewSize();
                if (size != null) {
                    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
                    height = size.getWidth();
                    width = size.getHeight();
                }
            }
        } else {
            if (mCamera2Source != null) {
                Size size = mCamera2Source.getPreviewSize();
                if (size != null) {
                    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
                    height = size.getWidth();
                    width = size.getHeight();
                }
            }
        }

        //RESIZE PREVIEW IGNORING ASPECT RATIO. THIS IS ESSENTIAL.
        int newWidth = (height * screenWidth) / screenHeight;

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;
        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int) (((float) layoutWidth / (float) newWidth) * height);
        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int) (((float) layoutHeight / (float) height) * newWidth);
        }
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        }
        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    public void createCameraSource(boolean hasBorder) {
        mHasBorder = hasBorder;

        FaceDetector previewFaceDetector = new FaceDetector.Builder(mContext.getApplicationContext())
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
//                .setProminentFaceOnly(true)
                .setTrackingEnabled(true)
                .build();

        if (previewFaceDetector.isOperational()) {
            previewFaceDetector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());
        } else {
            Toast.makeText(mContext.getApplicationContext(), "FACE DETECTION NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        }

        if (useCamera2) {
            mCamera2Source = new Camera2Source.Builder(mContext.getApplicationContext(), previewFaceDetector)
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_FRONT)
                    .build();

            // If camera2 hardware level is legacy, camera2 is not native.
            // We will use camera1.
            if (mCamera2Source.isCamera2Native()) {
                startCameraSource(mOverlay);
            } else {
                useCamera2 = false;
                createCameraSource(mHasBorder);
            }
        } else {
            mCameraSource = new CameraSource.Builder(mContext.getApplicationContext(), previewFaceDetector)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(30.0f)
                    .build();

            startCameraSource(mOverlay);
        }
    }

    public synchronized void setFaceDetectorListener(FaceDetectorListener listener) {
        this.mFaceDetectorListener = listener;
    }

    public synchronized void setPictureCapturedListener(PictureCapturedListener listener) {
        this.mPictureCapturedListener = listener;
    }

    public synchronized void setVideoCapturedListener(VideoCapturedListener listener) {
        this.mVideoCapturedListener = listener;
    }

    public synchronized void removeAllListeners() {
        this.mFaceDetectorListener = null;
        this.mPictureCapturedListener = null;
        this.mVideoCapturedListener = null;
    }

    public void captureImage() {
        if (useCamera2) {
            mCamera2Source.takePicture(this, this);
        } else {
            mCameraSource.takePicture(this, this);
        }
    }

    public void recordVideo(CameraQuality quality) {
        mIsRecordingVideo = true;
        if (useCamera2) {
            mCamera2Source.recordVideo(quality, createVideoPath(true), this);
        } else {
            mCameraSource.recordVideo(quality, createVideoPath(true), this);
        }
    }

    public void stopVideo() {
        if (useCamera2) {
            mCamera2Source.stopVideo();
            mCamera2Source.stop();
        } else {
            mCameraSource.stopVideo();
            mCameraSource.stop();
        }
    }

    /**
     * Is record in onProcess
     */
    public boolean isRecordingVideo() {
        return mIsRecordingVideo;
    }

    @NonNull
    private String createVideoPath(boolean isInternal) {
        String fileName = String.valueOf(new Random(System.nanoTime()).nextInt());
        File folderName = null;
        if (!isInternal) {
            folderName = getContext().getExternalCacheDir();
        }
        if (folderName == null) {
            folderName = getContext().getCacheDir();
        }
        mVideoPath = new File(folderName, fileName + ".mp4").getAbsolutePath();
        return mVideoPath;
    }

    @Override
    public void onShutter() {

    }

    @Override
    public void onPictureTaken(Bitmap picture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        Log.d(TAG, "PICTURE TAKEN FROM CAMERA");
        if (mPictureCapturedListener != null) {
            mPictureCapturedListener.onPictureCaptured(bytes, mSensorOrientation);
        }
    }

    @Override
    public void onVideoStart() {
        Log.d(TAG, "onVideoStart: ");
    }

    @Override
    public void onVideoStop(String videoFile) {
        mIsRecordingVideo = false;
        Log.d(TAG, "onVideoStop: ");
    }

    @Override
    public void onVideoError(String error) {
        Log.d(TAG, "onVideoError: ");
    }

    /**
     * Get current video path
     *
     * @return full video path
     */
    @Nullable
    public String getVideoPath() {
        if (mVideoPath != null && mVideoPath.isEmpty()) {
            mVideoPath = null;
        }
        return mVideoPath;
    }

    public byte[] getVideo() {
        File file = new File(mVideoPath);
        byte[] video = new byte[(int) file.length()];

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(video);
            fileInputStream.close();
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.logVideo(video);
        return video;
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mOverlay);
        }
    }

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
            mFaceGraphic = new FaceGraphic(overlay, GraphicFaceTracker.this, mHasBorder);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

            if (mFaceDetectorListener == null) return;

            PointF currentPosition = face.getPosition();
            if (mPreviousPosition != null && currentPosition != null) {
                double offset = Math.sqrt(Math.pow(currentPosition.x - mPreviousPosition.x, 2)
                        + Math.pow(currentPosition.y - mPreviousPosition.y, 2));
                if (offset > 5) {
                    mFaceDetectorListener.onShakingCamera(true);
                } else {
                    mFaceDetectorListener.onShakingCamera(false);
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

            FaceDetectorListener listener = mFaceDetectorListener;
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
            mFaceGraphic.goneFace();
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mFaceGraphic.goneFace();
            mOverlay.remove(mFaceGraphic);

            if (mFaceDetectorListener != null) {
                mFaceDetectorListener.onFaceLost();
                mFaceDetectorListener.onFaceInCenter(false);
            }
        }

        @Override
        public void onInArea(boolean isInArea) {
            if (mFaceDetectorListener != null) {
                mFaceDetectorListener.onFaceInCenter(isInArea);
            }
        }
    }

    public void startCameraSource(GraphicOverlayView mGraphicOverlay) {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                mContext.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog((Activity) mContext, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (useCamera2) {
            if (mCamera2Source != null) {
                try {
                    start(mCamera2Source, mGraphicOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source 2.", e);
                    mCamera2Source.release();
                    mCamera2Source = null;
                }
            }
        } else {
            if (mCameraSource != null) {
                try {
                    start(mCameraSource, mGraphicOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source.", e);
                    mCameraSource.release();
                    mCameraSource = null;
                }
            }
        }
    }
}
