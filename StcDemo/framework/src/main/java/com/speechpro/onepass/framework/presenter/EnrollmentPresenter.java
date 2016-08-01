package com.speechpro.onepass.framework.presenter;

import android.content.Context;
import com.speechpro.onepass.core.exception.BadRequestException;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.InternalServerException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.exceptions.RecorderException;
import com.speechpro.onepass.framework.injection.PerActivity;
import com.speechpro.onepass.framework.media.AudioRecorder;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.VoiceSample;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.util.Constants;
import com.speechpro.onepass.framework.util.Util;

import java.util.LinkedList;
import java.util.Queue;

import static com.speechpro.onepass.framework.util.Constants.ENROLLMENT_TIMEOUT;

/**
 * @author volobuev
 * @since 18.02.2016
 */
@PerActivity
public class EnrollmentPresenter extends BasePresenter {

    private Queue<Episode> episodes;
    private Episode currentEpisode;
    private String userId;

    public EnrollmentPresenter(IModel model, PreviewCallback previewCallback, Context context, String userId) {
        super(model, previewCallback, context);
        initialize(userId);
        episodes = new LinkedList<>();
        episodes.add(new Episode(R.string.episode1, R.string.enroll_phrases_1, R.string.phrase_dynamic_1));
        episodes.add(new Episode(R.string.episode2, R.string.enroll_phrases_2, R.string.phrase_dynamic_1));
        episodes.add(new Episode(R.string.episode3, R.string.enroll_phrases_3, R.string.phrase_dynamic_1));
    }

    @Override
    public void onStartRecording() {
        if (recorder == null) {
            recorder = new AudioRecorder();
        }
        super.onStartRecording();
    }

    @Override
    protected void addVoiceSample(byte[] pcmBytes, String passphrase) throws CoreException {
        getModel().addEnrollmentVoice(new VoiceSample(pcmBytes, passphrase, Constants.SAMPLE_RATE));
    }

    @Override
    protected void addFaceSample(byte[] faceBytes) throws CoreException {
        getModel().addEnrollmentFace(new FaceSample(faceBytes));
    }

    @Override
    protected void addVideo(byte[] video) throws CoreException {
        throw new RuntimeException("Method addVideo(byte[] video) is not available in enrollment process.");
    }


    @Override
    public String getPassphrase() {
        if (currentEpisode != null) {
            return context.getString(currentEpisode.getPhraseDynamic());
        }
        return null;
    }

    @Override
    public boolean getResult() {
        return getModel().isFullEnroll(userId);
    }

    @Override
    protected void delete() throws CoreException {
        getModel().deletePerson(userId);
    }

    @Override
    public Episode getEpisode() {
        currentEpisode = episodes.poll();
        return currentEpisode;
    }

    @Override
    public int getRecordingTimeout() {
        return ENROLLMENT_TIMEOUT;
    }

    public void processAudio() throws CoreException {
        try {
            byte[] pcmBytes = recorder.getMedia();
            Util.logPcm(pcmBytes);
            addVoiceSample(pcmBytes, getPassphrase());
        } catch (RecorderException ex) {
            toast(R.string.toast_unknown_error);
        }
    }

    public void processPhoto() throws CoreException {
        addFaceSample(previewCallback.getImage());
    }

    private void initialize(String userId) {
        this.userId = userId;
        getModel().createPerson(userId);
    }

}
