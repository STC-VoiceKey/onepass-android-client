package com.speechpro.onepass.framework.ui.fragment.verify;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.media.AudioListener;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;
import com.speechpro.onepass.framework.ui.fragment.BaseFragment;
import com.speechpro.onepass.framework.ui.listeners.RecButtonListener;
import com.speechpro.onepass.framework.ui.view.RecButtonView;
import com.speechpro.onepass.framework.ui.view.soundwave.SoundwaveSurfaceView;

/**
 * @author Alexander Grigal
 */
public class VerifyStaticVoiceFragment extends BaseFragment implements AudioListener, RecButtonListener {

    private final static String TAG = VerifyStaticVoiceFragment.class.getSimpleName();

    private View mProgressBar;
    private LinearLayout mMain;

    private SoundwaveSurfaceView mSoundwaveSurfaceView;

    private RecButtonView mRecButton;

    private VerificationActivity mActivity;
    private VerificationPresenter mPresenter;

    private Handler mHandler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.f_verify_voice, container, false);


        mActivity = (VerificationActivity) getActivity();
        mPresenter = (VerificationPresenter) mActivity.getPresenter();

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
        mPresenter.cancelRecording();
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

        mProgressBar = view.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mMain = (LinearLayout) view.findViewById(R.id.main_layout);

        TextView titleText = (TextView) view.findViewById(R.id.title_text);
        titleText.setText(R.string.pronounce_password_phrase);

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
            mPresenter.processStaticAudio(result);
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

                    mPresenter.restartTransaction();
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

