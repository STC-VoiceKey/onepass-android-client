package com.speechpro.onepass.framework.presenter;

import android.util.Log;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.VerificationSession;
import com.speechpro.onepass.framework.media.AudioHelper;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.Video;
import com.speechpro.onepass.framework.model.data.VoiceSample;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.util.Constants;

import static com.speechpro.onepass.framework.util.Constants.VERIFICATION_TIMEOUT;

/**
 * @author volobuev
 * @since 18.02.2016
 */
public class VerificationPresenter extends BasePresenter {

    private static final String TAG = VerificationPresenter.class.getSimpleName();

    private Boolean             mResult;
    private VerificationSession mSession;
    private String              mUserId;

    public VerificationPresenter(IModel model, BaseActivity activity, String userId) {
        super(model, activity);
        this.mUserId = userId;
        initialize(userId);
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
        getModel().addVerificationVideo(new Video(video, mSession.getPassphrase()));
    }

    @Override
    public boolean getResult() throws CoreException {
        if (mResult == null) {
            mResult = getModel().getVerificationResult();
        }
        return mResult;
    }

    @Override
    public String getPassphrase() {
        return mSession.getPassphrase();
    }

    //It doesn't use in verification flow
    @Override
    public Episode getEpisode() {
        return null;
    }

    @Override
    public void restartSession() {
        this.mSession = getModel().startVerification(mUserId);
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
        return true;
    }


    public void processVideo(byte[] video) throws CoreException {
        Log.d(TAG, "processVideo");
        addVideo(video);
    }

    private void initialize(String userId) {
        this.mSession = getModel().startVerification(userId);
    }

}
