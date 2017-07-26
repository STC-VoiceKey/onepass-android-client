package com.speechpro.onepass.framework.ui.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.FileObserver;
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

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;
import com.speechpro.onepass.framework.ui.listeners.CameraCallbackListener;
import com.speechpro.onepass.framework.ui.listeners.VerifyCameraCallbackListener;
import com.speechpro.onepass.framework.ui.view.BaseCameraView;
import com.speechpro.onepass.framework.ui.view.MaleMaskView;
import com.speechpro.onepass.framework.util.NumberMapper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.speechpro.onepass.framework.util.Constants.RECORD_TICK;

/**
 * @author volobuev
 * @since 02.09.16
 */
public class VerifyFragment extends BaseFragment
        implements CameraCallbackListener, VerifyCameraCallbackListener, SensorEventListener {

    private final static String TAG = VerifyFragment.class.getName();

    private final static int FACE_DETECTED_TIME = 2000;
    private final static int VERIFY_TIME = 5000;
    private final static short THREAD_COUNT = 5;

    private BaseCameraView mCameraView;
    private String mPassword;
    private String mPassphrase;
    private boolean mIsRecording;

    private ProgressBar mProgressBar;

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

    private ImageView mRecImageView ;
    private TextView mHintTextView;
    private boolean mIsFaceDetectionNotSupported;

    private VerificationPresenter mPresenter;
    private VerificationActivity mActivity;

    private int mProgressValue;
    private boolean mIsProgressTimer;
    private volatile boolean mIsProgressFaceDetectedTimer;

    private SensorManager mSensorManager;
    private Sensor mLight;

    private volatile boolean mIsOtherFaces;
    private volatile boolean mIsFaceOff;
    private volatile boolean mIsPoorLight;

    private Handler mHandler = new Handler();
    private ExecutorService mService = Executors.newFixedThreadPool(THREAD_COUNT);

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
            processVideo();
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

    public static VerifyFragment newInstance() {
        Bundle args = new Bundle();
        VerifyFragment fragment = new VerifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

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

        mMaleMaskView = (MaleMaskView) view.findViewById(R.id.male_mask_view);

        mCameraView = (BaseCameraView) view.findViewById(R.id.camera_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mMainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
        mProgressLayout = (RelativeLayout) view.findViewById(R.id.progress_layout);
        mWarningLayout = (LinearLayout) view.findViewById(R.id.warning_layout);
        mNumbersLayout = (RelativeLayout) view.findViewById(R.id.numbers_layout);

        mFacesImg = (ImageView) view.findViewById(R.id.faces_img);
        mFaceOffImg = (ImageView) view.findViewById(R.id.face_off_img);
        mLightImg = (ImageView) view.findViewById(R.id.light_img);

        mHintTextView = (TextView) view.findViewById(R.id.hint);
        mRecImageView = (ImageView) view.findViewById(R.id.rec_img_view);
        mRecImageView .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecImageView.setEnabled(false);
                startVerification();
            }
        });

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
        mIsOtherFaces = true;
        mIsRecording = false;

        mCameraView.setListener(this);

        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mNumbersLayout.setVisibility(GONE);
        mWarningLayout.setVisibility(VISIBLE);
        if (mCameraView.isRecordingVideo()) {
            return;
        }
        mCameraView.tryToStartPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        mCameraView.removeListener();
        mCameraView.tryToStop();
        mSensorManager.unregisterListener(this);

        mProgressTimer.cancel();
        mIsProgressTimer = false;
        mFaceDetectedTimer.cancel();
        mIsProgressFaceDetectedTimer = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
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
        Log.d(TAG, "onFaceDetected: \n" +
                "mIsPoorLight " + mIsPoorLight + "\n" +
                "mIsProgressFaceDetectedTimer " + mIsProgressFaceDetectedTimer + "\n" +
                "isRecordingVideo " + mCameraView.isRecordingVideo());

        mIsFaceOff = false;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMaleMaskView != null) {
                    mMaleMaskView.hide();
                    mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_white_48dp);
                }
            }
        });

        if (!mIsFaceOff && !mIsOtherFaces && !mIsPoorLight && !mIsProgressFaceDetectedTimer
                && !mCameraView.isRecordingVideo()) {
            Log.d(TAG, "start verification");
            mIsProgressFaceDetectedTimer = true;
            mFaceDetectedTimer.start();
        }
    }

    private void stopTimer() {
        mFaceDetectedTimer.cancel();
        mIsProgressFaceDetectedTimer = false;
    }

    @Override
    public void onFaceLost() {
        stopTimer();
        if (mMaleMaskView != null) {
            mMaleMaskView.show();
            mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_red_48dp);
        }
    }

    @Override
    public void onEyesOpen(boolean isOpen) {

    }

    @Override
    public void onFaceCount(int count) {
        if (!mIsProgressTimer) {
            mIsOtherFaces = count <= 1 ? false : true;
            if (count == 0) {
                onFaceLost();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWarningLayout.setVisibility(View.VISIBLE);
                        mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_red_48dp);
                        mFacesImg.setImageResource(R.drawable.ic_other_faces_white_48dp);
                        mIsFaceOff = true;
                        mIsOtherFaces = false;
                    }
                });
            } else if (count > 1) {
                stopTimer();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWarningLayout.setVisibility(View.VISIBLE);
                        mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_white_48dp);
                        mFacesImg.setImageResource(R.drawable.ic_other_faces_red_48dp);
                        mIsFaceOff = false;
                        mIsOtherFaces = true;
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mFaceOffImg.setImageResource(R.drawable.ic_face_off_center_white_48dp);
                        mFacesImg.setImageResource(R.drawable.ic_other_faces_white_48dp);
                        mIsFaceOff = false;
                        mIsOtherFaces = false;
                    }
                });
            }
        }
    }

    @Override
    public void onFaceInCenter(boolean isInCenter) {

    }

    @Override
    public void onShakingCamera(boolean isShaking) {

    }

    @Override
    public void onFaceDetectionNotSupported() {
        mIsFaceDetectionNotSupported = true;
        mHintTextView.setVisibility(VISIBLE);
        mRecImageView.setVisibility(VISIBLE);
        mRecImageView.setEnabled(true);
    }

    @Override
    public void onVideoCaptured(final String path) {
        FileObserver observer = new FileObserver(path, FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String additionalPath) {
                if (event == FileObserver.CLOSE_WRITE) {
                    if (mIsRecording) {
                        Log.d(TAG, "CAPTURED VIDEO SAVED (CODE: " + event + ") IN " + path);
                        mIsRecording = false;
                        mPresenter.playOtherActivePlayer();
                    } else {
                        boolean delResult = new File(path).delete();
                        Log.d(TAG,
                                (delResult ? "CAPTURED VIDEO REMOVED FROM " : "CAPTURED VIDEO NOT REMOVED FROM ") + path);
                    }
                    stopWatching();
                }
            }
        };
        observer.startWatching();
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

    private void startVerification() {
        if (!mIsRecording) {
            mCameraView.removeListener();
            mSensorManager.unregisterListener(this);

            mNumbersLayout.setVisibility(VISIBLE);
            mWarningLayout.setVisibility(GONE);
            mProgressBar.setProgress(0);
            mProgressValue = 0;
            mProgressBar.setVisibility(VISIBLE);
            mProgressBar.setMax(VERIFY_TIME);
            Log.d(TAG, "VIDEO RECORDING STARTED");
            if (!mPassword.isEmpty()) {
                mPassphraseText.setVisibility(GONE);
                setNumbers(mPassword, mNum0, mNum1, mNum2, mNum3, mNum4);
            } else {
                mPassphraseText.setText(mPassphrase);
                mPassphraseText.setVisibility(VISIBLE);
                if (getView() != null)
                    Snackbar.make(getView(), R.string.languages_do_not_match, Snackbar.LENGTH_SHORT).show();
            }
            Log.d(TAG, "startVerification password: " + mPassword + " passphrase: " + mPassphrase);
            mIsRecording = true;
            mPresenter.pauseOtherActivePlayer();
            mCameraView.captureVideo(true);
            mProgressTimer.start();
        }
    }

    private void setNumbers(String mPassphrase, TextView... mTextViews) {
        char[] mPass = mPassphrase.toCharArray();
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setText("" + mPass[i]);
            mTextViews[i].setVisibility(VISIBLE);
        }
    }

    private void loading(boolean isShowing) {
        Log.d(TAG, "Loading view onProcess.");
        Runnable task;
        if (isShowing) {
            task = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Progress is visible");
                    mMainLayout.setVisibility(GONE);
                    mProgressLayout.setVisibility(VISIBLE);
                    if (mIsFaceDetectionNotSupported) {
                        mHintTextView.setVisibility(GONE);
                        mRecImageView .setVisibility(GONE);
                    }
                }
            };
        } else {
            task = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Main is visible");
                    mMainLayout.setVisibility(VISIBLE);
                    mProgressLayout.setVisibility(GONE);
                    if (mIsFaceDetectionNotSupported) {
                        mHintTextView.setVisibility(VISIBLE);
                        mRecImageView.setVisibility(VISIBLE);
                    }
                }
            };
        }
        mHandler.post(task);
        Log.d(TAG, "Loading view was posted.");
    }

    private void showErrorDialogFragment(String descriptionError) {
        mActivity.showErrorFragment(this, getString(R.string.give_it_another_video), descriptionError);
    }

    private void processVideo() {
        Log.d(TAG, "processVideo: ");
        if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
            loading(true);
            mService.execute(new Runnable() {
                @Override
                public void run() {
                    if (mCameraView != null && mCameraView.isRecordingVideo()) {
                        mCameraView.stopVideoCapturing();
                        try {
                            mCameraView.tryToStop();
                        } catch (RuntimeException e) {
                            Log.e(TAG, "CAMERA TRY TO STOP: " + e.getMessage());
                        }
                        Log.d(TAG, "VIDEO RECORDING STOPPED");

                        try {
                            Log.d(TAG, "Processing video...");
                            mPresenter.processVideo(mCameraView.getVideo());
                            mPresenter.getResult();
                            Log.d(TAG, "Ready to go to next episode.");
                            mActivity.nextEpisode();
                        } catch (CoreException ex) {
                            Log.d(TAG, "CoreException");
                            mPresenter.restartSession();
                            loading(false);
                            final String descriptionError = ((RestException) ex).reason;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                                        showErrorDialogFragment(descriptionError);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
