package com.speechpro.onepass.framework.presenter;

import android.util.Pair;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.VoiceSample;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.util.Constants;

import java.util.LinkedList;
import java.util.Queue;

import static com.speechpro.onepass.framework.util.Constants.ENROLLMENT_TIMEOUT;

/**
 * @author volobuev
 * @since 18.02.2016
 */
public class EnrollmentPresenter extends BasePresenter {

    private static final String TAG = EnrollmentPresenter.class.getSimpleName();

    private Queue<Episode> mEpisodes;
    private Episode mCurrentEpisode;
    private String mUserId;

    public EnrollmentPresenter(IModel model, BaseActivity activity, String userId) {
        super(model, activity);
        mUserId = userId;
        mEpisodes = new LinkedList<>();
        mEpisodes.add(new Episode(R.string.episode1, mBaseActivity.getApplicationContext().getString(R.string.enroll_phrases_1)));
        mEpisodes.add(new Episode(R.string.episode2, mBaseActivity.getApplicationContext().getString(R.string.enroll_phrases_2)));
        mEpisodes.add(new Episode(R.string.episode3));
    }

    @Override
    protected void addDynamicVoiceSample(byte[] pcmBytes, String passphrase) throws CoreException {
        getModel().addEnrollmentDynamicVoice(new VoiceSample(pcmBytes, passphrase, Constants.SAMPLE_RATE));
    }

    @Override
    protected void addStaticVoiceSample(byte[] pcmBytes) throws CoreException {
        getModel().addEnrollmentStaticVoice(new VoiceSample(pcmBytes, Constants.SAMPLE_RATE));
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
    public void init() throws InternetConnectionException, RestException {
        getModel().startSession();
        getModel().startRegistrationTransaction(mUserId);
    }

    @Override
    public String getPassphrase() {
        if (mCurrentEpisode != null) {
            return mCurrentEpisode.getPhraseDynamic(mBaseActivity.getApplicationContext());
        }
        return null;
    }

    @Override
    public Boolean getResult() throws CoreException {
        return getModel().isFullEnroll(mUserId);
    }

    //It doesn't use in enrollment flow
    @Override
    public Pair<Boolean, String> getResultWithMessage() throws CoreException {
        return null;
    }

    @Override
    protected void delete() throws CoreException {
        getModel().deletePerson(mUserId);
    }

    @Override
    public void restartTransaction() {
//        getModel().startVerificationTransaction(mUserId);
    }

    @Override
    public Episode getEpisode() {
        mCurrentEpisode = mEpisodes.poll();
        return mCurrentEpisode;
    }

    @Override
    public int getRecordingTimeout() {
        return ENROLLMENT_TIMEOUT;
    }

}
