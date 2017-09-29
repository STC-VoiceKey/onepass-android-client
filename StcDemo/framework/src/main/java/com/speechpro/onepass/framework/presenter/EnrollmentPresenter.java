package com.speechpro.onepass.framework.presenter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.media.AudioListener;
import com.speechpro.onepass.framework.media.AudioRecorder;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.VoiceSample;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.util.BitmapUtil;
import com.speechpro.onepass.framework.util.Constants;
import com.speechpro.onepass.framework.util.Util;

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
    private AudioRecorder mAudioRecorder;

    public EnrollmentPresenter(IModel model, BaseActivity activity, String userId) {
        super(model, activity);
        initialize(userId);
        mEpisodes = new LinkedList<>();
        mEpisodes.add(new Episode(R.string.episode1, mBaseActivity.getApplicationContext().getString(R.string.enroll_phrases_1)));
        mEpisodes.add(new Episode(R.string.episode2, mBaseActivity.getApplicationContext().getString(R.string.enroll_phrases_2)));
        mEpisodes.add(new Episode(R.string.episode3));
    }

    public void startRecording(AudioListener mAudioListener) {
        Log.i(TAG, "Recording is started.");
        mAudioRecorder = new AudioRecorder(mAudioListener);
        mAudioRecorder.start();
    }

    public void releaseRecorder() {
        Log.i(TAG, "Recording is released.");
        mAudioRecorder.release();
    }

    public void stopRecording() {
        Log.i(TAG, "Recording is stopped.");
        mAudioRecorder.stop();
    }

    public void removeAudioListener() {
        Log.i(TAG, "AudioListener remove");
        if (mAudioRecorder != null)
            mAudioRecorder.removeAudioListener();
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
        if (mCurrentEpisode != null) {
            return mCurrentEpisode.getPhraseDynamic(mBaseActivity.getApplicationContext());
        }
        return null;
    }

    @Override
    public boolean getResult() throws CoreException {
        return getModel().isFullEnroll(mUserId);
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


    public void processAudio(byte[] pcmBytes) throws CoreException {
        Util.logVoice(pcmBytes);
        addVoiceSample(pcmBytes, getPassphrase());
    }

    public void processPhoto(byte[] img, int degrees) throws CoreException {
        Pair<Integer, Integer> resolution = BitmapUtil.getPictureResolution(img);

        ///this is bug samsung
        if (resolution.first > resolution.second) {
            degrees = (degrees + 270) % 360;
        }

        Log.d(TAG, "processPhoto: " + resolution.first + " " + resolution.second);

        byte[] rotatedData = BitmapUtil.rotatePicture(img,
                resolution.first,
                resolution.second,
                degrees);

        byte[] resizedPicture = BitmapUtil.resizedPicture(rotatedData, 240, 320);

        Util.logFaces(resizedPicture);
        addFaceSample(resizedPicture);
    }

    private void initialize(String userId) {
        try {
            getModel().startSession();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        this.mUserId = userId;
        getModel().startRegistrationTransaction(userId);
    }

}
