package com.speechpro.onepass.framework.presenter;

import android.util.Pair;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.core.exception.CoreException;
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

    private Pair<Boolean, String> mResultWithMsg;
    private String mUserId;

    public VerificationPresenter(IModel model, BaseActivity activity, String userId) {
        super(model, activity);
        mUserId = userId;
    }

    @Override
    protected void addDynamicVoiceSample(byte[] pcmBytes, String passphrase) throws CoreException {
        getModel().addVerificationDynamicVoice(new VoiceSample(pcmBytes, passphrase, Constants.SAMPLE_RATE));
    }

    @Override
    protected void addStaticVoiceSample(byte[] pcmBytes) throws CoreException {
        getModel().addVerificationStaticVoice(new VoiceSample(pcmBytes, Constants.SAMPLE_RATE));
    }

    @Override
    protected void addFaceSample(byte[] faceBytes) throws CoreException {
        getModel().addVerificationFace(new FaceSample(faceBytes));
    }

    @Override
    protected void addVideo(byte[] video) throws CoreException {
        getModel().addVerificationVideo(new Video(video, getModel().getVerificationPassphrase()));
    }

    @Override
    public Boolean getResult() throws CoreException {
        return null;
    }

    @Override
    public Pair<Boolean, String> getResultWithMessage() throws CoreException {
        if (mResultWithMsg == null) {
            mResultWithMsg = getModel().getVerificationResultWithMessage();
        }
        return mResultWithMsg;
    }

    @Override
    public void init() throws InternetConnectionException, RestException {
        getModel().startSession();
        getModel().startVerificationTransaction(mUserId);
    }

    @Override
    public String getPassphrase() {
        return getModel().getVerificationPassphrase();
    }

    //It doesn't use in verification flow
    @Override
    public Episode getEpisode() {
        return null;
    }

    @Override
    public void restartTransaction() {
        getModel().startVerificationTransaction(mUserId);
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
        addVideo(video);
    }

}
