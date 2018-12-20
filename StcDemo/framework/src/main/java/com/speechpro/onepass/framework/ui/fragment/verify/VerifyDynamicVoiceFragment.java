package com.speechpro.onepass.framework.ui.fragment.verify;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.speechpro.onepass.framework.util.NumberMapper;

/**
 * Created by Alexander Grigal on 28.02.18.
 */
public class VerifyDynamicVoiceFragment extends BaseFragment implements AudioListener, RecButtonListener {

    private final static String TAG = VerifyDynamicVoiceFragment.class.getSimpleName();

    private View mProgressBar;
    private LinearLayout mMain;

    private SoundwaveSurfaceView mSoundwaveSurfaceView;

    private RecButtonView mRecButton;

    private TextView mNum0;
    private TextView mNum1;
    private TextView mNum2;
    private TextView mNum3;
    private TextView mNum4;

    private VerificationActivity mActivity;
    private VerificationPresenter mPresenter;

    private Handler mHandler = new Handler();

    private String mPassphrase;
    private String mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.f_verify_voice, container, false);

        mActivity = (VerificationActivity) getActivity();
        mPresenter = (VerificationPresenter) mActivity.getPresenter();

        setupUI(view);

        clearPassphrase();
        initPassphrase();

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

        clearPassphrase();
        initPassphrase();
    }

    private void clearPassphrase() {
        mNum0.setText("‐");
        mNum1.setText("‐");
        mNum2.setText("‐");
        mNum3.setText("‐");
        mNum4.setText("‐");
    }

    private void initPassphrase() {
        mPassphrase = mPresenter.getPassphrase();
        mPassword = NumberMapper.convert(getActivity(), mPassphrase);

        setNumbers(mPassword,
                mNum0,
                mNum1,
                mNum2,
                mNum3,
                mNum4
        );
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

        mNum0 = (TextView) view.findViewById(R.id.num0);
        mNum1 = (TextView) view.findViewById(R.id.num1);
        mNum2 = (TextView) view.findViewById(R.id.num2);
        mNum3 = (TextView) view.findViewById(R.id.num3);
        mNum4 = (TextView) view.findViewById(R.id.num4);

        mProgressBar = view.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mMain = (LinearLayout) view.findViewById(R.id.main_layout);

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
            mPresenter.processDynamicAudio(result);
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

