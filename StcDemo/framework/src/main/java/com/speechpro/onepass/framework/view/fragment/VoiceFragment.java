package com.speechpro.onepass.framework.view.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.util.NumberMapper;
import com.speechpro.onepass.framework.view.MediaView;
import com.speechpro.onepass.framework.view.activity.BaseActivity;

import static com.speechpro.onepass.framework.util.Constants.ENROLLMENT_TIMEOUT;
import static com.speechpro.onepass.framework.util.Constants.RECORD_TICK;

/**
 * @author volobuev
 * @since 15.03.16
 */
public class VoiceFragment extends BaseFragment implements MediaView {

    private final static String TAG = "VoiceFragment";

    private LinearLayout recLayout;
    private LinearLayout processingLayout;

    private LinearLayout failedLayout;
    private LinearLayout qualityLayout;
    private LinearLayout pronunciationLayout;
    private LinearLayout shortLayout;

    private DonutProgress progress;

    private TextView enroll;
    private TextView textEpisode;
    private Button voiceButton;
    private Button retakeButton;

    private BaseActivity activity;
    private EnrollmentPresenter presenter;

    private Episode episode;

    private int progressValue;
    private boolean isRecording = false;

    private CountDownTimer recordTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_voice, container, false);

        activity = (BaseActivity) getActivity();
        presenter = (EnrollmentPresenter) activity.getPresenter();
        presenter.setMediaView(this);
        episode = presenter.getEpisode();

        recLayout = (LinearLayout) view.findViewById(R.id.record);
        processingLayout = (LinearLayout) view.findViewById(R.id.processing);
        progress = (DonutProgress) view.findViewById(R.id.progress);

        failedLayout = (LinearLayout) view.findViewById(R.id.recording_failed);
        qualityLayout = (LinearLayout) view.findViewById(R.id.record_quality_layout);
        pronunciationLayout = (LinearLayout) view.findViewById(R.id.pronunciation_layout);
        shortLayout = (LinearLayout) view.findViewById(R.id.short_layout);

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
        isRecording = true;
        progress.setVisibility(View.VISIBLE);

        progress.setMax(ENROLLMENT_TIMEOUT);
        progress.setProgress(0);
        updateCancelHandler();

        progressValue = 0;
        progress.setProgress(progressValue);
        if (recordTimer != null) {
            recordTimer.cancel();
        }
        recordTimer = new CountDownTimer(ENROLLMENT_TIMEOUT, RECORD_TICK) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isRecording) {
                    countProgress();
                }
            }

            @Override
            public void onFinish() {
                if (isRecording) {
                    countProgress();
                    presenter.onStopRecordingByButton();
                    stop();
                }
            }

            void countProgress(){
                progressValue += RECORD_TICK;
                progress.setProgress(progressValue);
            }
        };
        Log.d(TAG, "Audio recording is starting");
        presenter.onStartRecording();
        recordTimer.start();
        voiceButton.setText(getString(R.string.stop_button));

    }

    @Override
    public void stop() {
        isRecording = false;
        if (recordTimer != null){
            recordTimer.cancel();
        }
        if (isVisible()) {
            recLayout.setVisibility(View.GONE);
            processingLayout.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);

            voiceButton.setEnabled(false);
            try {
                presenter.processAudio();
                activity.nextEpisode();
            } catch (CoreException ex) {
                RestException restException = (RestException) ex;
                processingLayout.setVisibility(View.GONE);
                failedLayout.setVisibility(View.VISIBLE);
                voiceButton.setText(getString(R.string.start_button));
                parseReason(restException.reason);
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
        if (!presenter.isRecording) {
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

    private void parseReason(String reason) {
        if (reason.contains("Can't create segmentation")) {
            shortLayout.setVisibility(View.GONE);
            pronunciationLayout.setVisibility(View.GONE);
            qualityLayout.setVisibility(View.VISIBLE);
        } else if (reason.contains("poor password pronunciation")) {
            qualityLayout.setVisibility(View.GONE);
            shortLayout.setVisibility(View.GONE);
            pronunciationLayout.setVisibility(View.VISIBLE);
        } else if (reason.contains("too short")) {
            qualityLayout.setVisibility(View.GONE);
            pronunciationLayout.setVisibility(View.GONE);
            shortLayout.setVisibility(View.VISIBLE);
        }
    }

}

