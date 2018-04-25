package com.speechpro.onepass.framework.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.TextureView;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;

import static android.media.MediaRecorder.AudioEncoder.AAC;
import static android.media.MediaRecorder.OutputFormat.THREE_GPP;
import static android.media.MediaRecorder.VideoEncoder.H264;

/**
 * @author volobuev
 * @since 12.08.16
 */
public abstract class BaseCameraView extends AutoFitTextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = BaseCameraView.class.getSimpleName();

    protected final static int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    protected final static int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    protected final static int   RESOLUTION_Y     = 480;
    protected final static float RESOLUTION_RATIO = (float) 4 / 3;

    private final static int DEFAULT_X         = 85;
    private final static int DEFAULT_Y         = 75;
    private final static int DEFAULT_TOLERANCE = 10;

    protected final Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private int mFaceXPercent;
    private int mFaceYPercent;
    private int mFaceTolerancePercent;

    protected int mTopMaxPosition;
    protected int mBottomMaxPosition;
    protected int mLeftMaxPosition;
    protected int mRightMaxPosition;
    protected int mTopMinPosition;
    protected int mBottomMinPosition;
    protected int mLeftMinPosition;
    protected int mRightMinPosition;

    protected Integer       mSensorOrientation;
    protected MediaRecorder mMediaRecorder;

//    private CameraCallbackListener mListener;
    private BaseActivity           mActivity;
    private String                 mVideoPath;

    private boolean mIsRecordingVideo;

    public BaseCameraView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
        mFaceXPercent = DEFAULT_X;
        mFaceYPercent = DEFAULT_Y;
        mFaceTolerancePercent = DEFAULT_TOLERANCE;
    }

    public BaseCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
        setAttributes(attrs);
    }

    public BaseCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
        setAttributes(attrs);
    }

//    public synchronized void setListener(CameraCallbackListener mListener) {
//        this.mListener = mListener;
//    }

//    public synchronized void removeAllListeners() {
//        this.mListener = null;
//    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        openCamera(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        configureTransform(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public abstract void tryToStartPreview();

    public abstract void tryToStop();

    /**
     * Single image capture
     */
    public abstract void captureImage();

    /**
     * Video capture
     */
    public abstract void captureVideo(boolean isInternal);

    /**
     * Stop video recording
     */
    public abstract void stopVideoCapturing();

    /**
     * Get current video path
     *
     * @return full video path
     */
    @Nullable
    public String getVideoPath() {
        if (mVideoPath != null && mVideoPath.isEmpty()) { mVideoPath = null; }
        return mVideoPath;
    }

    public byte[] getVideo() {
        File file = new File(mVideoPath);
        byte[] video = new byte[(int) file.length()];

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(video);
            fileInputStream.close();
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.logVideo(video);
        return video;
    }

    /**
     * Is record in onProcess
     */
    public boolean isRecordingVideo() {
        return mIsRecordingVideo;
    }

    protected abstract void openCamera(int width, int height);

    protected abstract void configureTransform(int width, int height);

    protected BaseActivity getActivity() {
//        if (mActivity != null) { return mActivity; }
//        if (mListener == null) { return null; }
//        if (mListener instanceof BaseActivity) { return mActivity = (BaseActivity) mListener; }
//        if (mListener instanceof BaseFragment) {
//            BaseFragment fragment = (BaseFragment) mListener;
//            return mActivity = (BaseActivity) fragment.getActivity();
//        }
        return null;
    }

//    protected synchronized CameraCallbackListener getListener() {
//        return mListener;
//    }

    protected void updateTexturePreviewMatrix(int previewWidth, int previewHeight) {
        float ratio       = (float) getHeight() / (float) getWidth();
        float aspectRatio = (float) previewHeight / (float) previewWidth;

        Matrix txForm = new Matrix();
        getTransform(txForm);
        float delta = ratio / aspectRatio;
        if (Math.abs(1.0 - delta) < 0.01) {
            txForm.setScale(1 / delta, 1);
        } else if (delta < 1) {
            txForm.setScale(1 / delta, 1);
            txForm.postTranslate(((((float) getWidth() * delta) - (float) getWidth()) / 2), 0);
        } else {
            txForm.setScale(delta, 1);
            txForm.postTranslate((((float) getWidth() / delta) - (float) getWidth()) / 2, 0);
        }
        setTransform(txForm);
    }

    protected void calculatePositions(int width, int height) {
        float x              = (float) width;
        float y              = (float) height;
        int   xDelta         = (int) (x - ((x * mFaceXPercent) / 100)) / 2;
        int   yDelta         = (int) (y - ((y * mFaceYPercent) / 100)) / 2;
        int   toleranceDelta = (int) (y * mFaceTolerancePercent / 100);
        mTopMaxPosition = yDelta - toleranceDelta;
        mBottomMaxPosition = height - yDelta + toleranceDelta;
        mLeftMaxPosition = xDelta - toleranceDelta;
        mRightMaxPosition = width - xDelta;
        mTopMinPosition = yDelta + toleranceDelta;
        mBottomMinPosition = height - yDelta - toleranceDelta;
        mLeftMinPosition = xDelta + toleranceDelta;
        mRightMinPosition = width - xDelta - toleranceDelta;
    }

    @NonNull
    protected String createVideoPath(boolean isInternal) {
        String fileName = String.valueOf(new Random(System.nanoTime()).nextInt());
        File   folderName = null;
        if (!isInternal) { folderName = getContext().getExternalCacheDir(); }
        if (folderName == null) { folderName = getContext().getCacheDir(); }
        mVideoPath = new File(folderName, fileName + ".3gp").getAbsolutePath();
        return mVideoPath;
    }

    protected void setIsVideoRecording(boolean isVideoRecording) {
        mIsRecordingVideo = isVideoRecording;
    }

    /**
     * Set up video recorder
     *
     * @param isInternal is internal storage
     * @throws IOException file access exception
     */
    protected void setUpMediaRecorder(boolean isInternal, int width, int height, int degrees) throws IOException {
        if (getActivity() == null) { return; }

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setOutputFormat(THREE_GPP);
        mMediaRecorder.setVideoEncodingBitRate(500000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoEncoder(H264);
        mMediaRecorder.setAudioEncoder(AAC);
        mMediaRecorder.setAudioEncodingBitRate(48000);
        mMediaRecorder.setAudioSamplingRate(11025);
        mMediaRecorder.setAudioChannels(1);
        mMediaRecorder.setOutputFile(createVideoPath(isInternal));
//        mMediaRecorder.setVideoSize(320, 240);
        mMediaRecorder.setVideoSize(width, height);
        mMediaRecorder.setOrientationHint(degrees);
        mMediaRecorder.prepare();
    }

    /**
     * Checks face detection.
     *
     * @param inArea
     */
    protected void checkFaceDetection(boolean inArea) {
//        if (inArea) {
//            if (getListener() != null) {
//                getListener().onFaceDetected();
//            }
//        } else {
//            if (getListener() != null) {
//                getListener().onFaceLost();
//            }
//        }
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray attrsArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BaseCameraView, 0, 0);
        try {
            mFaceXPercent = attrsArray.getInt(R.styleable.BaseCameraView_xFaceRequired, DEFAULT_X);
        } catch (Exception e) {
            mFaceXPercent = DEFAULT_X;
        }
        try {
            mFaceTolerancePercent = attrsArray.getInt(R.styleable.BaseCameraView_widthTolerance, DEFAULT_TOLERANCE);
        } catch (Exception e) {
            mFaceTolerancePercent = DEFAULT_TOLERANCE;
        }
        try {
            mFaceYPercent = attrsArray.getInt(R.styleable.BaseCameraView_yFaceRequired, DEFAULT_Y);
        } catch (Exception e) {
            mFaceYPercent = DEFAULT_Y;
        } finally {
            attrsArray.recycle();
        }

        if ((mFaceXPercent > 90) || (mFaceXPercent < 10)) { mFaceXPercent = DEFAULT_X; }
        if ((mFaceYPercent > 90) || (mFaceYPercent < 10)) { mFaceYPercent = DEFAULT_Y; }
        if (mFaceTolerancePercent <= 5) { mFaceTolerancePercent = DEFAULT_TOLERANCE; }
    }
}
