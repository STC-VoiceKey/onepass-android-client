package com.speechpro.onepass.framework.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.injection.components.UIComponent;
import com.speechpro.onepass.framework.presenter.BasePresenter;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.util.NumberMapper;
import com.speechpro.onepass.framework.view.MediaView;
import com.speechpro.onepass.framework.view.activity.BaseActivity;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author volobuev
 * @since 15.03.16
 */
public class VoiceFragment extends BaseFragment implements MediaView {

    private LinearLayout recLayout;
    private LinearLayout processingLayout;
    private LinearLayout failedLayout;

    private ProgressBar progress;

    private TextView enroll;
    private TextView textEpisode;
    private Button   voiceButton;
    private Button   retakeButton;

    private BaseActivity        activity;
    private EnrollmentPresenter presenter;

    private Episode episode;

    private boolean isRecording = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_voice, container, false);

        activity = (BaseActivity) getActivity();
        presenter = (EnrollmentPresenter) activity.getPresenter();
        presenter.setMediaView(this);
        episode = presenter.getEpisode();

        recLayout = (LinearLayout) view.findViewById(R.id.record);
        processingLayout = (LinearLayout) view.findViewById(R.id.processing);
        failedLayout = (LinearLayout) view.findViewById(R.id.recording_failed);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        processingLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);
        recLayout.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);

        enroll = (TextView) view.findViewById(R.id.text_enroll);
        textEpisode = (TextView) view.findViewById(R.id.text_episode);

        voiceButton = (Button) view.findViewById(R.id.voice_button);
        retakeButton = (Button) view.findViewById(R.id.retake_button);

        setupUI();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void start() {
        progress.setVisibility(View.VISIBLE);
        updateCancelHandler();
        isRecording = true;
        voiceButton.setText(getString(R.string.stop_button));
        presenter.onStartRecording();
    }

    @Override
    public void stop() {
        if (isVisible()) {
            recLayout.setVisibility(View.GONE);
            processingLayout.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);

            isRecording = false;
            voiceButton.setEnabled(false);
            if (presenter.processAudio()) {
                activity.nextEpisode();
            } else {
                processingLayout.setVisibility(View.GONE);
                failedLayout.setVisibility(View.VISIBLE);
                voiceButton.setText(getString(R.string.start_button));
            }
            voiceButton.setEnabled(true);
        }
    }

    @Override
    public void toast(int resId) {
        Toast toast = Toast.makeText(activity, resId, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected boolean isEnrollment() {
        return presenter instanceof EnrollmentPresenter;
    }

    private void setupUI() {

        if (episode != null) {
            textEpisode.setText(getString(episode.getStage()));
            enroll.setText(getString(episode.getEnrollPhrases()));
        } else {
            textEpisode.setVisibility(View.INVISIBLE);
            enroll.setText(NumberMapper.convert(presenter.getPassphrase()));
        }
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceButtonClick();
            }
        });
        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retake();
            }
        });
    }

    private void voiceButtonClick() {
        if (!isRecording) {
            start();
        } else {
            presenter.onStopRecordingByButton();
            stop();
        }
    }

    private void retake() {
        recLayout.setVisibility(View.VISIBLE);
        processingLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);
        progress.setVisibility(View.INVISIBLE);
    }
}

