package com.speechpro.onepass.framework.ui.fragment.enroll;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.media.AudioListener;
import com.speechpro.onepass.framework.presenter.BasePresenter;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.fragment.BaseFragment;
import com.speechpro.onepass.framework.ui.listeners.RecButtonListener;
import com.speechpro.onepass.framework.ui.view.RecButtonView;
import com.speechpro.onepass.framework.ui.view.soundwave.SoundwaveSurfaceView;

/**
 * @author Alexander Grigal
 */
public abstract class EnrollVoiceFragment extends BaseFragment implements AudioListener, RecButtonListener {

    View mProgressBar;
    LinearLayout mMain;

    Episode mEpisode;
    TextView mEpisodeText;

    RecButtonView mRecButton;

    EnrollmentActivity mActivity;
    BasePresenter mPresenter;

    Handler mHandler = new Handler();

    private SoundwaveSurfaceView mSoundwaveSurfaceView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_enroll_voice, container, false);

        mActivity = (EnrollmentActivity) getActivity();
        mPresenter = mActivity.getPresenter();
        mEpisode = mPresenter.getEpisode();

        setupUI(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMain.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mSoundwaveSurfaceView.setVisibility(View.VISIBLE);

        mRecButton.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.cancelRecording();
        mSoundwaveSurfaceView.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.removeAudioListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupUI(View view) {
        mEpisodeText = (TextView) view.findViewById(R.id.episode_text);

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

    }

    @Override
    public void stop(byte[] result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                mMain.setVisibility(View.GONE);
                mRecButton.setVisibility(View.GONE);
                mSoundwaveSurfaceView.setVisibility(View.GONE);
            }
        });
    }

    protected void showErrorDialogFragment(String descriptionError) {
        if (!mActivity.isFinishing())
            mActivity.showErrorFragment(this, getString(R.string.give_it_another_recording), descriptionError);
    }

    @Override
    public void onProcess(final short amplitude) {
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
        mPresenter.stopRecording();
        mPresenter.playOtherActivePlayer();
    }

    private void startRecording() {
        mRecButton.startProgress();
        mPresenter.startRecording(this);
        mPresenter.pauseOtherActivePlayer();
    }

}
