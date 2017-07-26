package com.speechpro.onepass.framework.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.hardware.camera2.*;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import com.speechpro.onepass.framework.ui.listeners.EnrollCameraCallbackListener;
import com.speechpro.onepass.framework.ui.listeners.VerifyCameraCallbackListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Camera view for Android 5.0 or higher
 *
 * @author volobuev
 * @since 15.08.16
 */
@TargetApi(21)
public class LollipopCameraView extends BaseCameraView {

    private final static String TAG = "LollipopCameraView";

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private Size mPreviewSize;
    private Size mVideoSize;

    private CameraDevice  mCameraDevice;
    private HandlerThread mBackgroundThread;
    private Handler       mBackgroundHandler;

    private CameraCaptureSession   mPreviewSession;
    private CaptureRequest.Builder mPreviewBuilder;
    private CaptureRequest         mPreviewRequest;
    private Surface                mRecorderSurface;
    private ImageReader            mImageReader;

    private boolean mFaceDetectSupported;
    private int     mFaceDetectMode;
    private Rect    mActiveArrayRect;
    private String  mCameraId;

    private              int mState                       = 0;
    private final static int STATE_NOT_READY              = 0;
    private final static int STATE_PREVIEW                = 1;
    private final static int FACE_LOCKED                  = 2;
    private final static int VIDEO_RECORDED               = 3;
    private final static int STATE_WAITING_LOCK           = 4;
    private final static int STATE_WAITING_PRECAPTURE     = 5;
    private final static int STATE_WAITING_NON_PRECAPTURE = 6;
    private final static int STATE_PICTURE_TAKEN          = 7;


    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            Log.d(TAG, "CAMERA OPENED");
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            configureTransform(getWidth(), getHeight());
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

    };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            getActivity().runOnUiThread(new ImageSaver(reader));
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private float mDeltaX;
        private float mDeltaY;
        private int repeats = 0;

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    checkFace(result);
                    break;
                }
                case FACE_LOCKED: {
                    repeats = 0;
                    lockFocus();
                    break;
                }
                case STATE_WAITING_LOCK: {
                    if (repeats > 30) {
                        captureStillPicture();
                    }
                    repeats++;
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                               CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    if (repeats > 30) {
                        captureStillPicture();
                    }
                    repeats++;
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    if (repeats > 30) {
                        captureStillPicture();
                    }
                    repeats++;
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session,
                                    @NonNull CaptureRequest request,
                                    @NonNull CaptureFailure failure) {

        }

        /**
         * Face checking
         *
         * @param result camera2 capture result
         */
        private void checkFace(CaptureResult result) {
            if (!mFaceDetectSupported) { return; }
            if ((mDeltaX == 0) || (mDeltaY == 0)) {
                mDeltaX = (float) getWidth() / mActiveArrayRect.height();
                mDeltaY = (float) getHeight() / mActiveArrayRect.width();
            }
            Face faceArray[] = result.get(CaptureResult.STATISTICS_FACES);
            if (faceArray != null && faceArray.length == 0 ) {
                if (getListener() != null) { getListener().onFaceLost(); }
            } else if (faceArray != null && faceArray.length == 1) {
                boolean inArea;
                Face    face       = faceArray[0];
                Rect    faceBounds = face.getBounds();
                float   minX, minY, maxX, maxY;
                if (faceBounds.bottom > faceBounds.top) {
                    minX = (float) faceBounds.top;
                    maxX = (float) faceBounds.bottom;
                } else {
                    minX = (float) faceBounds.bottom;
                    maxX = (float) faceBounds.top;
                }
                if (faceBounds.right > faceBounds.left) {
                    minY = (float) faceBounds.left;
                    maxY = (float) faceBounds.right;
                } else {
                    minY = (float) faceBounds.right;
                    maxY = (float) faceBounds.left;
                }
                inArea = ((mTopMaxPosition < minX) &&
                          (mBottomMaxPosition > maxX) &&
                          (mLeftMaxPosition < minY) &&
                          (mRightMaxPosition > maxY));
                checkFaceDetection(inArea);
                if (getListener() != null) {
//                    if (mSensorOrientation == SENSOR_ORIENTATION_INVERSE_DEGREES) {
//                        getListener().onFaceDebug(inArea,
//                                                  getWidth() - minX * mDeltaX,
//                                                  getHeight() - minY * mDeltaY,
//                                                  getWidth() - maxX * mDeltaX,
//                                                  getHeight() - maxY * mDeltaY);
//                    } else if (mSensorOrientation == SENSOR_ORIENTATION_DEFAULT_DEGREES) {
//                        getListener().onFaceDebug(inArea,
//                                                  minX * mDeltaX,
//                                                  minY * mDeltaY,
//                                                  maxX * mDeltaX,
//                                                  maxY * mDeltaY);
//                    }
                }
            } else {
                if (getListener() != null) {
                    getListener().onFaceLost();
                }
            }
        }
    };

    public LollipopCameraView(Context context) {
        super(context);
    }

    public LollipopCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LollipopCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void openCamera(int width, int height) {
        Log.d(TAG, "CAMERA OPENING");
        if ((getActivity() == null) || getActivity().isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                return;
            }
            int camerasCounter = manager.getCameraIdList().length;
            if (camerasCounter > 1) {
                mCameraId = manager.getCameraIdList()[1];
            } else if (camerasCounter == 1) {
                mCameraId = manager.getCameraIdList()[0];
            } else { return; }


            CameraCharacteristics  characteristics = manager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map             = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                return;
                //TODO: error init
            }
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class), getWidth());
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);
                setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                updateTexturePreviewMatrix(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class), getHeight());
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), height, width, mVideoSize);
                setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                updateTexturePreviewMatrix(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(width, height);
            mActiveArrayRect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            if (mActiveArrayRect != null) {
                calculatePositions(mActiveArrayRect.width(), mActiveArrayRect.height());
            } else { calculatePositions(width, height); }
            mMediaRecorder = new MediaRecorder();
            mImageReader = ImageReader.newInstance(mVideoSize.getWidth(), mVideoSize.getHeight(), ImageFormat.JPEG, 1);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
            int[] fD = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
            if (fD == null) {
                mFaceDetectMode = -1;
                mFaceDetectSupported = false;
            } else {
                try {
                    int maxFD = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
                    if (fD.length > 0) {
                        List<Integer> fdList = new ArrayList<>();
                        for (int faceD : fD) {
                            fdList.add(faceD);
                        }
                        if (maxFD > 0) {
                            mFaceDetectSupported = true;
                            mFaceDetectMode = Collections.max(fdList);
                        }
                    }
                } catch (NullPointerException e) {
                    mFaceDetectMode = -1;
                    mFaceDetectSupported = false;
                }
            }

            manager.openCamera(mCameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            Log.d(TAG, "CAMERA ACCESS ERROR");
        } catch (NullPointerException e) {
            Log.d(TAG, "CAMERA BUSY");
        } catch (SecurityException e) {
            Log.d(TAG, "CAMERA REQUIRES PERMISSION");
        } catch (InterruptedException e) {
        }
    }

    @Override
    protected void configureTransform(int width, int height) {
        if (null == mPreviewSize || null == getActivity()) {
            return;
        }
        int    rotation   = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix     = new Matrix();
        RectF  viewRect   = new RectF(0, 0, width, height);
        RectF  bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float  centerX    = viewRect.centerX();
        float  centerY    = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) height / mPreviewSize.getHeight(), (float) width / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        setTransform(matrix);
    }

    private void startPreview() {
        try {
            closePreviewSession();
            SurfaceTexture texture = getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface surface = new Surface(texture);
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                                               new CameraCaptureSession.StateCallback() {

                                                   @Override
                                                   public void onConfigured(
                                                           @NonNull CameraCaptureSession cameraCaptureSession) {
                                                       if (null == mCameraDevice) {
                                                           return;
                                                       }
                                                       mPreviewSession = cameraCaptureSession;
                                                       try {
                                                           mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                                                               CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                                           if (mFaceDetectSupported) {
                                                               mPreviewBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
                                                                                   mFaceDetectMode);
                                                           }
                                                           mPreviewRequest = mPreviewBuilder.build();
                                                           mPreviewSession.setRepeatingRequest(mPreviewRequest,
                                                                                               mCaptureCallback,
                                                                                               mBackgroundHandler);
                                                           mState = STATE_PREVIEW;
                                                           cameraReady();
                                                       } catch (CameraAccessException e) {
                                                           e.printStackTrace();
                                                       }
                                                   }

                                                   @Override
                                                   public void onConfigureFailed(
                                                           @NonNull CameraCaptureSession cameraCaptureSession) {
                                                       //TODO: init error
                                                   }
                                               },
                                               null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) { return; }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewRequest = mPreviewBuilder.build();
            mPreviewSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
            mState = STATE_PREVIEW;
            cameraReady();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
        } finally {
            mCameraOpenCloseLock.release();
        }
        if (getListener() != null) { getListener().onCameraClose(); }
    }

    public void tryToStartPreview() {
        startBackgroundThread();
        if (isAvailable()) { openCamera(getWidth(), getHeight()); } else { setSurfaceTextureListener(this); }
    }

    public void tryToStop() {
        if (isRecordingVideo()) { stopVideoCapturing(); }
        closeCamera();
        stopBackgroundThread();
    }

    @Override
    public void captureImage() {
        mState = FACE_LOCKED;
    }

    @Override
    public void captureVideo(boolean isInternal) {
        if (mCameraDevice == null || !isAvailable() || mPreviewSize == null) {
            return;
        }
        try {
            if (mPreviewSession != null) { mPreviewSession.stopRepeating(); }
            closePreviewSession();
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            switch (mSensorOrientation) {
                case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                    setUpMediaRecorder(isInternal,
                                       mVideoSize.getWidth(),
                                       mVideoSize.getHeight(),
                                       DEFAULT_ORIENTATIONS.get(rotation));
                    break;
                case SENSOR_ORIENTATION_INVERSE_DEGREES:
                    setUpMediaRecorder(isInternal,
                                       mVideoSize.getWidth(),
                                       mVideoSize.getHeight(),
                                       INVERSE_ORIENTATIONS.get(rotation));
                    break;
            }
            SurfaceTexture texture = getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces       = new ArrayList<>();
            Surface       previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            mRecorderSurface = mMediaRecorder.getSurface();
            surfaces.add(mRecorderSurface);
            mPreviewBuilder.addTarget(mRecorderSurface);

            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mState = VIDEO_RECORDED;
                            setIsVideoRecording(true);
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //TODO: video configuration failed
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            //TODO: permissions error
        } catch (IOException e) {
            //TODO: file access error
        }
    }

    @Override
    public void stopVideoCapturing() {
        mState = STATE_NOT_READY;
        if (getListener() != null) {
            if (getListener() instanceof VerifyCameraCallbackListener) {
                ((VerifyCameraCallbackListener) getListener()).onVideoCaptured(getVideoPath());
            }
        }
        if (mPreviewSession != null) {
            try {
                mPreviewSession.stopRepeating();
                mPreviewSession.abortCaptures();
            } catch (CameraAccessException e) {
            }
        }
        try {
            mMediaRecorder.stop();
        } catch (RuntimeException ex) {
            //Ignore
        }
        mMediaRecorder.reset();
        setIsVideoRecording(false);
        startPreview();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;
            mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void runPrecaptureSequence() {
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = STATE_WAITING_PRECAPTURE;
            mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureStillPicture() {
        try {
            if (getActivity() == null || null == mCameraDevice) {
                return;
            }
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Capture COMPLETED");
                            unlockFocus();
                        }
                    });
                }
            };

            mPreviewSession.stopRepeating();
            mPreviewSession.capture(captureBuilder.build(), CaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void cameraReady() {
        if (getListener() != null && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getListener().onCameraReady(getMeasuredWidth(), getMeasuredHeight());
                }
            });
        }
    }

    private void unlockFocus() {
        try {
            if(mPreviewBuilder != null) {
                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                mState = STATE_PREVIEW;
                if (mPreviewSession != null) {
                    mPreviewSession.capture(mPreviewBuilder.build(), mCaptureCallback, mBackgroundHandler);
                    mPreviewSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getOrientation(int rotation) {
        return (DEFAULT_ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    private static Size chooseVideoSize(Size[] choices, int max) {
        List<Size> actualSizes = new ArrayList<>();
        for (Size size : choices) {
            if ((Math.abs(size.getWidth() - (size.getHeight() * RESOLUTION_RATIO)) < 0.05) &&
                size.getWidth() <= max &&
                size.getHeight() >= RESOLUTION_Y) { actualSizes.add(size); }
        }
        Size retSize = null;
        if (actualSizes.size() > 0) { return Collections.min(actualSizes, new CompareSizesByArea()); }
        for (Size size : choices) {
            if (retSize == null) { retSize = size; } else if ((retSize.getHeight() + retSize.getWidth()) <
                                                              (size.getHeight() + size.getWidth())) {
                retSize = size;
            }
        }
        return retSize;
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int        w         = aspectRatio.getWidth();
        int        h         = aspectRatio.getHeight();
        for (Size option : choices) {
            float wh = option.getHeight();
            float ww = (option.getWidth() * (float) h / (float) w);
            if ((Math.abs(wh - ww) < 0.05) &&
                option.getWidth() >= width && option.getHeight() >= height) { bigEnough.add(option); }
        }
        Size choice;
        if (bigEnough.size() > 0) { choice = Collections.min(bigEnough, new CompareSizesByArea()); } else {
            choice = choices[0];
        }
        return choice;
    }

    private class ImageSaver implements Runnable {
        private final ImageReader mReader;

        public ImageSaver(ImageReader reader) {
            this.mReader = reader;
        }

        @Override
        public void run() {
            Image      image  = mReader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[]     bytes  = new byte[buffer.remaining()];
            buffer.get(bytes);
            buffer.clear();
            image.close();
            if (getListener() != null && getListener() instanceof EnrollCameraCallbackListener) {
                ((EnrollCameraCallbackListener) getListener()).onPictureCaptured(bytes, 0);
            }
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
