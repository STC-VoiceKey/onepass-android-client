package com.speechpro.onepass.framework.presenter;

import android.content.Context;
import android.os.AsyncTask;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.VerificationSession;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.media.AudioRecorder;
import com.speechpro.onepass.framework.media.VideoRecorder;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.Video;
import com.speechpro.onepass.framework.model.data.VoiceSample;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.util.Constants;

import static com.speechpro.onepass.framework.util.Constants.VERIFICATION_TIMEOUT;

/**
 * @author volobuev
 * @since 18.02.2016
 */
public class VerificationPresenter extends BasePresenter {

    private VerificationSession session;
    private String              userId;

    public VerificationPresenter(IModel model, PreviewCallback previewCallback, Context context, String userId) {
        super(model, previewCallback, context);
        this.userId = userId;
        initialize(userId);
    }

    @Override
    public void onStartRecording() {
        recorder = new VideoRecorder(visionView.getCamera(), context);
        super.onStartRecording();
    }

    @Override
    protected void addVoiceSample(byte[] pcmBytes, String passphrase) throws CoreException {
        getModel().addVerificationVoice(new VoiceSample(pcmBytes, passphrase, Constants.SAMPLE_RATE));
    }

    @Override
    protected void addFaceSample(byte[] faceBytes) throws CoreException {
        getModel().addVerificationFace(new FaceSample(faceBytes));
    }

    @Override
    protected void addVideo(byte[] video) throws CoreException {
        getModel().addVerificationVideo(new Video(video, session.getPassphrase()));
    }

    @Override
    public boolean getResult() {
        return getModel().getVerificationResult();
    }

    @Override
    public String getPassphrase() {
        return session.getPassphrase();
    }

    //It doesn't use in verification flow
    @Override
    public Episode getEpisode() {
        return null;
    }

    public void restartSession() {
        this.session = getModel().startVerification(userId);
    }

    public boolean isRecPrepared(){
        return recorder.isPrepared();
    }

    @Override
    protected void delete() throws CoreException {
        getModel().deleteVerificationSession();
    }

    @Override
    public int getRecordingTimeout() {
        return VERIFICATION_TIMEOUT;
    }

    public boolean processVideo() {
        try {
            addVideo(recorder.getMedia());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void initialize(String userId) {
        this.session = getModel().startVerification(userId);
    }

}
