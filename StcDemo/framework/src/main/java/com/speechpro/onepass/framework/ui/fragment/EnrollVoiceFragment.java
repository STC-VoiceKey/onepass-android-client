package com.speechpro.onepass.framework.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.media.AudioListener;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.listeners.RecButtonListener;
import com.speechpro.onepass.framework.ui.view.RecButtonView;
import com.speechpro.onepass.framework.ui.view.soundwave.SoundwaveSurfaceView;

/**
 * @author volobuev
 * @since 15.03.16
 */
public class EnrollVoiceFragment extends BaseFragment implements AudioListener, RecButtonListener {

    private final static String TAG = EnrollVoiceFragment.class.getSimpleName();

    private View mProgressBar;
    private LinearLayout mMain;

    private SoundwaveSurfaceView mSoundwaveSurfaceView;

    private RecButtonView mRecButton;

    private TextView mEpisodeText;
    private TextView mTitleText;

    private TextView mNum0;
    private TextView mNum1;
    private TextView mNum2;
    private TextView mNum3;
    private TextView mNum4;
    private TextView mNum5;
    private TextView mNum6;
    private TextView mNum7;
    private TextView mNum8;
    private TextView mNum9;

    private LinearLayout mWarningLayout;
    private TextView mWarningText;

    private EnrollmentActivity mActivity;
    private EnrollmentPresenter mPresenter;

    private Episode mEpisode;

    private Handler mHandler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.f_voice, container, false);

        mActivity = (EnrollmentActivity) getActivity();
        mPresenter = (EnrollmentPresenter) mActivity.getPresenter();
        mEpisode = mPresenter.getEpisode();

        setupUI(view);

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mMain.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mSoundwaveSurfaceView.setVisibility(View.VISIBLE);

        mRecButton.setEnabled(true);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mSoundwaveSurfaceView.clear();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mPresenter.removeAudioListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    private void setupUI(View view) {
        Log.d(TAG, "setupUI");
        mEpisodeText = (TextView) view.findViewById(R.id.episode_text);
        mTitleText = (TextView) view.findViewById(R.id.title_text);

        mNum0 = (TextView) view.findViewById(R.id.num0);
        mNum1 = (TextView) view.findViewById(R.id.num1);
        mNum2 = (TextView) view.findViewById(R.id.num2);
        mNum3 = (TextView) view.findViewById(R.id.num3);
        mNum4 = (TextView) view.findViewById(R.id.num4);
        mNum5 = (TextView) view.findViewById(R.id.num5);
        mNum6 = (TextView) view.findViewById(R.id.num6);
        mNum7 = (TextView) view.findViewById(R.id.num7);
        mNum8 = (TextView) view.findViewById(R.id.num8);
        mNum9 = (TextView) view.findViewById(R.id.num9);

        mWarningLayout = (LinearLayout) view.findViewById(R.id.warning_layout);
        mWarningLayout.setVisibility(View.GONE);
        mWarningText = (TextView) view.findViewById(R.id.warning_text);
        mProgressBar = view.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mMain = (LinearLayout) view.findViewById(R.id.main_layout);

        if (mEpisode != null) {
            mEpisodeText.setText(getString(mEpisode.getStage()));
            setNumbers(mEpisode.getEnrollPhrases(),
                    mNum0,
                    mNum1,
                    mNum2,
                    mNum3,
                    mNum4,
                    mNum5,
                    mNum6,
                    mNum7,
                    mNum8,
                    mNum9);
        }

        mSoundwaveSurfaceView = (SoundwaveSurfaceView) view.findViewById(R.id.sound_view);

        mRecButton = (RecButtonView) view.findViewById(R.id.rec_img_view);
        mRecButton.setRecButtonListener(this);
        mRecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRecButton.isRunning()) {
                    startRecording();
                } else {
                    mRecButton.setEnabled(false);
                    mRecButton.stopProgress();
                }
            }
        });
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
    }

    @Override
    public void stop(byte[] result) {
        Log.d(TAG, "stop");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                mMain.setVisibility(View.GONE);
                mRecButton.setVisibility(View.GONE);
                mSoundwaveSurfaceView.setVisibility(View.GONE);
            }
        });
        try {
            mPresenter.processAudio(result);
            mActivity.nextEpisode();
        } catch (CoreException ex) {
            Log.d(TAG, ex.getMessage());
            mPresenter.releaseRecorder();
            Log.e(TAG, ex.toString());
            final String descriptionError = ((RestException) ex).reason;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showErrorDialogFragment(descriptionError);
                    mProgressBar.setVisibility(View.GONE);
                    mMain.setVisibility(View.VISIBLE);
                    mRecButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void showErrorDialogFragment(String descriptionError) {
        if (!mActivity.isFinishing())
            mActivity.showErrorFragment(this, getString(R.string.give_it_another_recording), descriptionError);
    }

    @Override
    public void onProcess(final short amplitude) {
//        Log.d(TAG, "onProcess: " + amplitude);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    mSoundwaveSurfaceView.addVerticalLine(((float) amplitude) / 10000);
                }
            }
        });
    }

    @Override
    public void onProgressFinish() {
        Log.d(TAG, "onProgressFinish: ");
        mPresenter.stopRecording();
        mPresenter.playOtherActivePlayer();
    }

    private void setNumbers(String mPassphrase, TextView... mTextViews) {
        char[] mPass = mPassphrase.toCharArray();
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setText("" + mPass[i]);
        }
    }

    private void startRecording() {
        mRecButton.startProgress();
        mPresenter.startRecording(this);
        mPresenter.pauseOtherActivePlayer();
    }

}

