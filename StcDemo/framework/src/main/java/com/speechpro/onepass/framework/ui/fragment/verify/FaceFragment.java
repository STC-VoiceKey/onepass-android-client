package com.speechpro.onepass.framework.ui.fragment.verify;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;
import com.speechpro.onepass.framework.ui.fragment.BaseFragment;
import com.speechpro.onepass.framework.ui.listeners.FaceDetectorListener;
import com.speechpro.onepass.framework.ui.view.GraphicOverlayView;
import com.speechpro.onepass.framework.ui.view.MaleMaskView;
import com.speechpro.onepass.framework.ui.view.camera.CameraSourcePreview;
import com.speechpro.onepass.framework.util.NumberMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.speechpro.onepass.framework.util.Constants.RECORD_TICK;

/**
 * @author volobuev
 * @since 02.09.16
 */
public abstract class FaceFragment extends BaseFragment
        implements FaceDetectorListener, SensorEventListener {

    private final static String TAG = FaceFragment.class.getSimpleName();

    private final static int FACE_DETECTED_TIME = 2000;
    private final static int DELAY_TIME = 1500;
    private final static int VERIFY_TIME = 5000;
    private final static short THREAD_COUNT = 5;

    protected Handler mHandler = new Handler();
    protected ExecutorService mService = Executors.newFixedThreadPool(THREAD_COUNT);

    protected CameraSourcePreview mCameraSourcePreview;
    protected VerificationPresenter mPresenter;
    protected VerificationActivity mActivity;
    protected boolean mIsRecording;
    protected SensorManager mSensorManager;

    private String mPassword;
    private String mPassphrase;

    private TextView mPassphraseText;
    private TextView mNum0;
    private TextView mNum1;
    private TextView mNum2;
    private TextView mNum3;
    private TextView mNum4;

    private RelativeLayout mMainLayout;
    private RelativeLayout mProgressLayout;
    private RelativeLayout mNumbersLayout;
    private LinearLayout mWarningLayout;

    private ImageView mFacesImg;
    private ImageView mFaceOffImg;
    private ImageView mLightImg;

    private MaleMaskView mMaleMaskView;

    private GraphicOverlayView mGraphicOverlay;

    private ProgressBar mProgressBar;
    private int mProgressValue;
    private boolean mIsProgressTimer;
    private volatile boolean mIsProgressFaceDetectedTimer;

    private Sensor mLight;

    private boolean mIsOtherFaces;
    private boolean mIsFaceOff;
    private boolean mIsPoorLight;

    private CountDownTimer mProgressTimer = new CountDownTimer(VERIFY_TIME, RECORD_TICK) {

        @Override
        public void onTick(long millisUntilFinished) {
            mProgressValue += RECORD_TICK;
            mProgressBar.setProgress(mProgressValue);
            mIsProgressTimer = true;
        }

        @Override
        public void onFinish() {
            mProgressBar.setProgress(VERIFY_TIME);
            process();
            mIsProgressTimer = false;
            Log.d(TAG, "onFinish progress timer: ");
        }

    };

    private CountDownTimer mFaceDetectedTimer = new CountDownTimer(FACE_DETECTED_TIME, RECORD_TICK) {

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "faceDetected tick");
            mIsProgressFaceDetectedTimer = true;
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "faceDetected finish");
            mIsProgressFaceDetectedTimer = false;
            startVerification();
        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mActivity = (VerificationActivity) getActivity();
        mPresenter = (VerificationPresenter) mActivity.getPresenter();

        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.f_video, container, false);

        mPassphraseText = (TextView) view.findViewById(R.id.passphrase_text);

        mNum0 = (TextView) view.findViewById(R.id.num0);
        mNum0.setVisibility(View.INVISIBLE);
        mNum1 = (TextView) view.findViewById(R.id.num1);
        mNum1.setVisibility(View.INVISIBLE);
        mNum2 = (TextView) view.findViewById(R.id.num2);
        mNum2.setVisibility(View.INVISIBLE);
        mNum3 = (TextView) view.findViewById(R.id.num3);
        mNum3.setVisibility(View.INVISIBLE);
        mNum4 = (TextView) view.findViewById(R.id.num4);
        mNum4.setVisibility(View.INVISIBLE);

        if (!mActivity.isDebugMode()) {
            mMaleMaskView = (MaleMaskView) view.findViewById(R.id.male_mask_view);
        }

        mCameraSourcePreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlayView) view.findViewById(R.id.faceOverlay);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mMainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
        mProgressLayout = (RelativeLayout) view.findViewById(R.id.progress_layout);
        mWarningLayout = (LinearLayout) view.findViewById(R.id.warning_layout);
        mNumbersLayout = (RelativeLayout) view.findViewById(R.id.numbers_layout);

        mFacesImg = (ImageView) view.findViewById(R.id.faces_img);
        mFaceOffImg = (ImageView) view.findViewById(R.id.face_off_img);
        mLightImg = (ImageView) view.findViewById(R.id.light_img);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        mPassphrase = mPresenter.getPassphrase();
        mPassword = NumberMapper.convert(getActivity(), mPassphrase);

        mIsFaceOff = true;
        mIsOtherFaces = false;
        mIsRecording = false;

        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mNumbersLayout.setVisibility(GONE);
        mWarningLayout.setVisibility(VISIBLE);

        mCameraSourcePreview.createCameraSource(mActivity.isDebugMode());
        mCameraSourcePreview.setFaceDetectorListener(this);
        mCameraSourcePreview.startCameraSource(mGraphicOverlay);

        mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_red_48dp);

        if (mMaleMaskView != null) {
            mMaleMaskView.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
//        mCameraView.removeAllListeners();
//        mCameraView.tryToStop();
        mSensorManager.unregisterListener(this);

        mProgressTimer.cancel();
        mIsProgressTimer = false;
        mFaceDetectedTimer.cancel();
        mIsProgressFaceDetectedTimer = false;

        mCameraSourcePreview.removeAllListeners();
        mCameraSourcePreview.stop();

        mPresenter.cancelRecording();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        mCameraSourcePreview.release();
        super.onDestroyView();
    }

    @Override
    public void onFaceDetected() {
        Log.d(TAG, "FACE DETECTED");
        if (!mIsOtherFaces && !mIsFaceOff && !mIsPoorLight) {
            if (!mIsProgressFaceDetectedTimer) {
                Log.d(TAG, "captureImage: ");
                Log.d(TAG, "start verification");
                mIsProgressFaceDetectedTimer = true;
                mFaceDetectedTimer.start();
            }
        } else {
            mIsProgressFaceDetectedTimer = false;
            mFaceDetectedTimer.cancel();
        }

    }

    private void stopTimer() {
        mFaceDetectedTimer.cancel();
        mIsProgressFaceDetectedTimer = false;
    }

    @Override
    public void onFaceLost() {
        Log.d(TAG, "FACE LOST");
        stopTimer();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFacesImg.setImageResource(R.drawable.ic_other_faces_white_48dp);
                mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_white_48dp);
            }
        });
    }

    @Override
    public void onEyesOpen(boolean isOpen) {

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
    public void onShakingCamera(boolean isShaking) {

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (!isAdded()) return;

//        float limit = 17f;
        float limit = 0f;

        if (event.values[0] < limit) {
            mLightImg.setImageResource(R.drawable.ic_light_red_48dp);
            mIsPoorLight = true;
        } else {
            mLightImg.setImageResource(R.drawable.ic_light_white_48dp);
            mIsPoorLight = false;
        }
    }

    protected void setNumbers(String mPassphrase, TextView... mTextViews) {
        char[] mPass = mPassphrase.toCharArray();
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setText("" + mPass[i]);
            mTextViews[i].setVisibility(VISIBLE);
        }
    }

    protected void loading(boolean isShowing) {
        Log.d(TAG, "Loading view onProcess.");
        Runnable task;
        if (isShowing) {
            task = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Progress is visible");
                    mMainLayout.setVisibility(GONE);
                    mProgressLayout.setVisibility(VISIBLE);
                }
            };
        } else {
            task = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Main is visible");
                    mMainLayout.setVisibility(VISIBLE);
                    mProgressLayout.setVisibility(GONE);
                }
            };
        }
        mHandler.post(task);
        Log.d(TAG, "Loading view was posted.");
    }

    protected void showError(String descriptionError) {
        mActivity.showErrorFragment(this, getString(R.string.give_it_another_video), descriptionError);
    }

    protected void initProgressBar() {
        mProgressBar.setProgress(0);
        mProgressValue = 0;
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.setMax(VERIFY_TIME);
    }

    protected void showPassphrase() {
        mPassphraseText.setVisibility(GONE);
        setNumbers(mPassword, mNum0, mNum1, mNum2, mNum3, mNum4);
    }

    protected void startVerificationCountDownTimer() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNumbersLayout.setVisibility(VISIBLE);
                mWarningLayout.setVisibility(GONE);
                initProgressBar();
                Log.d(TAG, "RECORDING STARTED");
                if (!mPassword.isEmpty()) {
                    showPassphrase();
                } else {
                    mPassphraseText.setText(mPassphrase);
                    mPassphraseText.setVisibility(VISIBLE);
                    if (getView() != null)
                        Snackbar.make(getView(), R.string.languages_do_not_match, Snackbar.LENGTH_SHORT).show();
                }
                Log.d(TAG, "startVerificationTransaction password: " + mPassword + " passphrase: " + mPassphrase);
                mIsRecording = true;

                mProgressTimer.start();

            }
        }, DELAY_TIME);
    }

    protected abstract void startVerification();

    protected abstract void process();

}
