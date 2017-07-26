package com.speechpro.onepass.framework.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.listeners.EnrollCameraCallbackListener;
import com.speechpro.onepass.framework.ui.listeners.VerifyCameraCallbackListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author volobuev
 * @since 12.08.16
 */
@TargetApi(19)
@SuppressWarnings("Deprecation")
public class KitKatCameraView extends BaseCameraView
        implements Camera.FaceDetectionListener, Camera.ShutterCallback, Camera.PictureCallback {

    private final static String TAG = KitKatCameraView.class.getName();

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private Camera.Size mCaptureSize;
    private Camera.Size mPreviewSize;

    private float maxResolution = -1;

    private boolean mIsFaceDetection;
    private boolean mIsReady;
    private boolean mFaceTaken;
    private int mRotation;
    private int mCurrentApiVersion;

    public KitKatCameraView(Context context) {
        super(context);
        init();
    }

    public KitKatCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KitKatCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCurrentApiVersion = android.os.Build.VERSION.SDK_INT;
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    @Override
    protected void openCamera(int width, int height) {
        if ((getActivity() == null) || getActivity().isFinishing()) {
            mIsReady = false;
            return;
        }
        if (mCameraInfo == null) {
            mCameraInfo = new Camera.CameraInfo();
        }
        try {
            mFaceTaken = false;
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                mIsReady = false;
                return;
            }
            int camerasCounter = Camera.getNumberOfCameras();
            int cameraId;
            if (camerasCounter > 1) {
                cameraId = 1;
            } else if (camerasCounter == 1) {
                cameraId = 0;
            } else {
                mIsReady = false;
                return;
            }

            Camera.getCameraInfo(cameraId, mCameraInfo);
            mSensorOrientation = mCameraInfo.orientation;
            mCamera = Camera.open(cameraId);

            if (mCameraInfo.canDisableShutterSound) {
                mCamera.enableShutterSound(false);
            }
            Camera.Parameters parameters = mCamera.getParameters();
            if (mCaptureSize == null)
                mCaptureSize = chooseVideoSize(getSupportedVideoSizes(mCamera),
                        parameters.getSupportedPictureSizes(),
                        RESOLUTION_RATIO,
                        RESOLUTION_Y);
            if (mPreviewSize == null)
                mPreviewSize = chooseOptimalSize(parameters.getSupportedPreviewSizes(), width, mCaptureSize);
            setAspectRatio(mPreviewSize.height, mPreviewSize.width);
            updateTexturePreviewMatrix(mPreviewSize.height, mPreviewSize.width);

            configureTransform(width, height);

            parameters.setPictureSize(mCaptureSize.width, mCaptureSize.height);
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);

            mMediaRecorder = new MediaRecorder();
            mIsReady = true;
            Log.d(TAG, "openCamera finished");
            if (isAvailable()) {
                startPreview();
            }
        } catch (InterruptedException | RuntimeException e) {
            Log.e(TAG, "Camera locked", e);
            this.getRootView().setBackgroundColor(R.color.black_transparent);
            Snackbar.make(this, R.string.camera_locked,
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private List<Camera.Size> getSupportedVideoSizes(Camera camera) {
        if (camera.getParameters().getSupportedVideoSizes() != null) {
            return camera.getParameters().getSupportedVideoSizes();
        } else {
            // Video sizes may be null, which indicates that all the supported
            // preview sizes are supported for video recording.
            return camera.getParameters().getSupportedPreviewSizes();
        }
    }

    private Camera.Size chooseOptimalSize(List<Camera.Size> supportedPreviewSizes,
                                          int height,
                                          Camera.Size captureSize) {

        List<Camera.Size> bigEnough = new ArrayList<>();
        int w = captureSize.width;
        int h = captureSize.height;
        for (Camera.Size option : supportedPreviewSizes) {
            float wh = option.height;
            float ww = (option.width * (float) h / (float) w);
            if ((Math.abs(wh - ww) < 0.05) && option.height <= height) {
                bigEnough.add(option);
            }
        }
        Camera.Size choice;
        Log.d(TAG, "chooseOptimalSize: " + bigEnough.size());
        if (bigEnough.size() > 0) {
            choice = Collections.max(bigEnough, new CompareSizesByArea());
        } else {
            choice = supportedPreviewSizes.get(0);
        }

        maxResolution = (((float) choice.height * (float) choice.width)) / (1024000.0f);

        return choice;
    }

    private Camera.Size chooseVideoSize(List<Camera.Size> supportedVideoSizes,
                                        List<Camera.Size> supportedPictureSizes,
                                        float ratio,
                                        int height) {

        List<Camera.Size> availableVideos = new ArrayList<>();
        for (Camera.Size size : supportedVideoSizes) {
            if ((Math.abs((float) size.width / (float) size.height) - ratio < 0.05) &&
                    (size.height >= height) &&
                    (supportedPictureSizes.contains(size))) {
                availableVideos.add(size);
            }
        }
        if (availableVideos.size() > 1) {
            return Collections.min(availableVideos, new CompareSizesByArea());
        } else if (availableVideos.size() == 1) {
            return availableVideos.get(0);
        } else {
            return supportedVideoSizes.get(0);
        }
    }

    @Override
    protected void configureTransform(int width, int height) {
        if (null == mPreviewSize || null == getActivity()) {
            return;
        }
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mRotation = (mCameraInfo.orientation + degrees) % 360;
            mRotation = (360 - mRotation) % 360;  // compensate the mirror
        } else {  // back-facing
            mRotation = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(mRotation);
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, width, height);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.height, mPreviewSize.width);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) height / mPreviewSize.height, (float) width / mPreviewSize.width);
            matrix.postScale(scale, scale, centerX, centerY);
            Log.d(TAG, "Scale set to " + scale);
        }
        setTransform(matrix);
    }

    @Override
    public void tryToStartPreview() {
        Log.d(TAG, "Try to start camera");
        if (mCamera == null && isAvailable()) {
            openCamera(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    public void tryToStop() {
        Log.d(TAG, "Try to stop camera");

        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        if (mCamera != null) {
            mCamera.setFaceDetectionListener(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        mCameraOpenCloseLock.release();
        if (getListener() != null) {
            getListener().onCameraClose();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: ");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "Surface destroyed");
        tryToStop();
        return super.onSurfaceTextureDestroyed(surfaceTexture);
    }

    private void startPreview() {
        try {
            setIsVideoRecording(false);
            Log.d(TAG, "Preview started");
            mCamera.setPreviewTexture(getSurfaceTexture());
            mCamera.startPreview();
            calculatePositions(getMeasuredHeight(), getMeasuredWidth());
            mCamera.setFaceDetectionListener(this);
            try {
                mCamera.startFaceDetection();
            } catch (IllegalArgumentException eae) {
                Log.d(TAG, "Face detection is unsupported.");
                if (getListener() != null) {
                    if (getListener() instanceof VerifyCameraCallbackListener) {
                        ((VerifyCameraCallbackListener) getListener()).onFaceDetectionNotSupported();
                    }
                }
            }
            cameraReady();
        } catch (IOException ioe) {
            Log.d(TAG, "Preview error");
        }
    }

    private void cameraReady() {
        Log.d(TAG, "Camera ready");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getListener() != null) {
                    getListener().onCameraReady(getMeasuredWidth(), getMeasuredHeight());
                }
            }
        }, 200);
    }

    @Override
    public void captureImage() {
        Log.d(TAG, "Picture capture request");
        if (mCamera != null && mIsReady) {
            mCamera.takePicture(this, null, this);
        }
    }

    @Override
    public void captureVideo(boolean isInternal) {
        Log.d(TAG, "Video capture request");
        if (mCamera != null && mIsReady) {
            try {
                mCamera.unlock();
            } catch (RuntimeException e) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.reset();
                }
                try {
                    mCamera.reconnect();
                } catch (IOException e1) {
                    mCamera.lock();
                } finally {
                    mCamera.unlock();
                }
            }
            try {
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                int rotation;
                if (mSensorOrientation == SENSOR_ORIENTATION_DEFAULT_DEGREES) {
                    rotation = mRotation;
                } else {
                    rotation = (360 - mRotation) % 360;
                }
                setUpMediaRecorder(isInternal, mCaptureSize.width, mCaptureSize.height, mSensorOrientation);
                mMediaRecorder.start();
                setIsVideoRecording(true);
            } catch (IOException e) {
                mCamera.lock();
            } catch (IllegalStateException e1) {
                mCamera.lock();
            }
        }
    }

    @Override
    public void stopVideoCapturing() {
        Log.d(TAG, "Video capture stopped");
        if (mCamera != null && mMediaRecorder != null && isRecordingVideo()) {
            if (getListener() != null) {
                if (getListener() instanceof VerifyCameraCallbackListener) {
                    ((VerifyCameraCallbackListener) getListener()).onVideoCaptured(getVideoPath());
                }
            }
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException ex) {
                //Ignore
            }
            mMediaRecorder.reset();
            setIsVideoRecording(false);
            try {
                mCamera.reconnect();
            } catch (IOException e) {
                mCamera.lock();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        } else {
            setIsVideoRecording(false);
        }
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {

        if (faces != null && getListener() != null) {
            getListener().onFaceCount(faces.length);
            Log.d(TAG, "faces detection: " + faces.length);
        }

        if (faces != null && faces.length == 0) {
            if (maxResolution >= 0.5f || (maxResolution < 0.5f && !mIsFaceDetection)) {
                if (getListener() != null) {
                    getListener().onFaceLost();
                    Log.d(TAG, "face not detection");
                }
            }
        } else if (faces != null && faces.length == 1) {
            Camera.Face face = faces[0];
            Rect faceBounds = face.rect;

            //float minX, minY, maxX, maxY;
            float top, bottom, left, right;
            top = (faceBounds.top + 1000) * getMeasuredWidth() / 2000;
            bottom = (faceBounds.bottom + 1000) * getMeasuredWidth() / 2000;
            left = (faceBounds.left + 1000) * getMeasuredHeight() / 2000;
            right = (faceBounds.right + 1000) * getMeasuredHeight() / 2000;
            boolean inArea = ((mTopMaxPosition < top) && (mTopMinPosition > top) &&     //vertical only
                    (mBottomMaxPosition > bottom) && (mBottomMinPosition < bottom) &&   //vertical only
                    (mLeftMaxPosition < left) && (mRightMaxPosition > right));

            boolean distance = true;
            boolean confidence = false;

            if (maxResolution < 0.5f) {
                confidence = true;
            } else {
                confidence = ((face.score > 50) || (face.score == 0));
            }

//            boolean confidence = ((face.score > 50) || (face.score == 0));

            //            if (face.rightEye != null &&
            //                face.leftEye != null &&
            //                (face.rightEye.x != -1000 || face.leftEye.y != -1000) &&
            //                (face.rightEye.x != 0 || face.leftEye.y != 0)) {
            //
            //                double distValue = Math.sqrt(Math.pow(face.rightEye.x + 2 - face.leftEye.x, 2) +
            //                                             Math.pow(face.rightEye.y - face.leftEye.y, 2));
            //                distance = 3.5 * distValue > 2000;
            //            }

            mIsFaceDetection = confidence && distance;
            checkFaceDetection(mIsFaceDetection);
        } else {
            if (getListener() != null) {
                getListener().onFaceLost();
            }
            Log.d(TAG, "face lost");
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(TAG, "PICTURE TAKEN FROM CAMERA");
        if (getListener() != null && getListener() instanceof EnrollCameraCallbackListener && !mFaceTaken) {
            mFaceTaken = true;
            ((EnrollCameraCallbackListener) getListener()).onPictureCaptured(data, mSensorOrientation);
        }

        Log.d(TAG, "SDK version is " + mCurrentApiVersion);
        if (mCurrentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
            mCamera.stopPreview();
            mCamera.startPreview();
            mCamera.startFaceDetection();
        }

    }

    @Override
    public void onShutter() {

    }

    static class CompareSizesByArea implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
        if (mCurrentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (mMediaRecorder != null && isRecordingVideo()) {
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException ex) {
                Log.e(TAG, ex.getMessage());
            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            setIsVideoRecording(false);
            if (mCamera != null) {
                try {
                    mCamera.reconnect();
                } catch (IOException e) {
                    mCamera.lock();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        } else {
            setIsVideoRecording(false);
        }
    }
}