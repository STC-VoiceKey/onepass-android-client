package com.speechpro.onepass.framework.ui.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.listeners.EnrollCameraCallbackListener;
import com.speechpro.onepass.framework.ui.view.CameraSourcePreview;
import com.speechpro.onepass.framework.ui.view.GraphicOverlayView;
import com.speechpro.onepass.framework.ui.view.MaleMaskView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author volobuev
 * @since 18.08.16
 */
public class EnrollFaceFragment extends BaseFragment implements EnrollCameraCallbackListener {

    private final static String TAG = EnrollFaceFragment.class.getName();

    private final static int FACE_FIXATE_TIME = 1000;
    private final static int CIRCLES_NUMBER = 20;
    private final static int TICK_TIME = FACE_FIXATE_TIME / CIRCLES_NUMBER;

    private EnrollmentPresenter mPresenter;
    private EnrollmentActivity mActivity;

    private MaleMaskView mMaleMaskView;

    private CameraSourcePreview mCameraSourcePreview;
    private GraphicOverlayView mGraphicOverlay;

    private View mProgressLayout;
    private LinearLayout mWarningLayout;

    private ImageView mEyesImg;
    private ImageView mFacesImg;
    private ImageView mFaceOffImg;
    private ImageView mLightImg;
    private ImageView mShakingImg;

    private boolean mIsEyesOpen = false;
    private boolean mIsOtherFaces = true;
    private boolean mIsFaceOff = true;
    private boolean mIsPoorLight = false;
    private boolean mIsShaking = true;

    private boolean mIsBusyCamera;
    private boolean mInProcessTimer;

    private SensorEventListener mSensorEventListener;

    private Handler mHandler = new Handler();
    private ExecutorService mService = Executors.newCachedThreadPool();

    private CountDownTimer mCountDownTimer = new CountDownTimer(FACE_FIXATE_TIME, TICK_TIME) {

        @Override
        public void onTick(long millisUntilFinished) {
            mInProcessTimer = true;
        }

        @Override
        public void onFinish() {
            if (getActivity() != null && !getActivity().isFinishing()) {
                mIsBusyCamera = true;
                Log.d(TAG, "Image capturing...");
                mCameraSourcePreview.captureImage();
                mInProcessTimer = false;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_face, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (EnrollmentActivity) getActivity();
        mPresenter = (EnrollmentPresenter) mActivity.getPresenter();

        mProgressLayout = view.findViewById(R.id.progress_layout);

        mWarningLayout = (LinearLayout) view.findViewById(R.id.warning_layout);

        mMaleMaskView = (MaleMaskView) view.findViewById(R.id.male_mask_view);

        mCameraSourcePreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlayView) view.findViewById(R.id.faceOverlay);

        mEyesImg = (ImageView) view.findViewById(R.id.eyes_img);
        mFacesImg = (ImageView) view.findViewById(R.id.faces_img);
        mFaceOffImg = (ImageView) view.findViewById(R.id.face_off_img);
        mLightImg = (ImageView) view.findViewById(R.id.light_img);
        mShakingImg = (ImageView) view.findViewById(R.id.shake_img);

        mSensorEventListener = new LightSensorEventListener();
        mActivity.setSensorListener(mSensorEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraSourcePreview.setListener(this);
        mCameraSourcePreview.startCameraSource(mGraphicOverlay);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraSourcePreview.removeListener();
        mCameraSourcePreview.stop();
    }

    @Override
    public void onDetach() {
        if (mActivity != null) {
            mActivity.removeSensorListener(mSensorEventListener);
        }
        super.onDetach();
    }

    @Override
    public void onPictureCaptured(final byte[] picture, final int degrees) {
        mService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressLayout.setVisibility(View.VISIBLE);
                            mCameraSourcePreview.stop();
                        }
                    });
                    mPresenter.processPhoto(picture, degrees);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.nextEpisode();
                        }
                    });
                } catch (CoreException ex) {
                    Log.e(TAG, ex.toString());

                    mIsBusyCamera = false;

                    final String descriptionError = ((RestException) ex).reason;

                    if (mActivity != null && !mActivity.isFinishing()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressLayout.setVisibility(View.INVISIBLE);
                                showError(descriptionError);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onEyesOpen(final boolean isOpen) {
        Log.d(TAG, "onEyesOpen: ");
        mIsEyesOpen = isOpen;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mEyesImg.setImageResource(isOpen
                        ? R.drawable.ic_eyes_white_48dp
                        : R.drawable.ic_eyes_red_48dp);
            }
        });
    }

    @Override
    public void onFaceCount(final int count) {
        Log.d(TAG, "onFaceCount: " + count);
        mIsOtherFaces = count <= 1 ? false : true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFacesImg.setImageResource(count == 1
                        ? R.drawable.ic_other_faces_white_48dp
                        : R.drawable.ic_other_faces_red_48dp
                );
            }
        });
    }

    @Override
    public void onFaceInCenter(final boolean isInCenter) {
        Log.d(TAG, "onFaceOff: " + isInCenter);
        mIsFaceOff = !isInCenter;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMaleMaskView != null) {
                    if (isInCenter) {
                        mMaleMaskView.hide();
                    } else {
                        mMaleMaskView.show();
                    }
                }
                mFaceOffImg.setImageResource(isInCenter
                        ? R.drawable.ic_face_off_center_white_48dp
                        : R.drawable.ic_face_off_center_red_48dp
                );
            }
        });
    }

    @Override
    public void onShakingCamera(final boolean isShaking) {
        Log.d(TAG, "onShakingCamera: " + isShaking);
        mIsShaking = isShaking;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mShakingImg.setImageResource(isShaking
                        ? R.drawable.ic_shaking_red_48dp
                        : R.drawable.ic_shaking_white_48dp);
            }
        });
    }

    private void showError(String descriptionError) {
        mActivity.showErrorFragment(this, getString(R.string.give_it_another_shot), descriptionError);
    }

    @Override
    public void onCameraReady(int width, int height) {
        Log.d(TAG, "CAMERA READY");
    }

    @Override
    public void onCameraError(int errorCode) {
        Log.d(TAG, "CAMERA DESCRIPTION_ERROR. CODE = " + errorCode);
    }

    @Override
    public void onCameraClose() {
        Log.d(TAG, "CAMERA CLOSED");
    }

    @Override
    public void onFaceDetected() {
        Log.d(TAG, "FACE DETECTED");
        if (!mIsBusyCamera) {
            if (mIsEyesOpen && !mIsOtherFaces && !mIsFaceOff && !mIsPoorLight && !mIsShaking) {
                Log.d(TAG, "captureImage: ");
                if (!mInProcessTimer) {
                    mCountDownTimer.start();
                }
                Log.d(TAG, "onFaceDetected: " + "mCountDownTimer.start()");
            } else {
                Log.d(TAG, "onFaceDetected: " + "mCountDownTimer.cancel()");
                mCountDownTimer.cancel();
                mInProcessTimer = false;
            }
        }
    }

    @Override
    public void onFaceLost() {
        Log.d(TAG, "FACE LOST");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mEyesImg.setImageResource(R.drawable.ic_eyes_white_48dp);
                mFacesImg.setImageResource(R.drawable.ic_other_faces_white_48dp);
                mShakingImg.setImageResource(R.drawable.ic_shaking_white_48dp);
            }
        });
    }

    private class LightSensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!isAdded()) return;

            float limit = 2f;

            if (event.values[0] < limit) {
                mWarningLayout.setVisibility(View.VISIBLE);
                mLightImg.setImageResource(R.drawable.ic_light_red_48dp);
                mIsPoorLight = true;
            } else {
                mIsPoorLight = false;
                mLightImg.setImageResource(R.drawable.ic_light_white_48dp);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
