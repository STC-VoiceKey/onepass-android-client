package com.speechpro.onepass.framework.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.media.AudioHelper;
import com.speechpro.onepass.framework.media.AudioListener;
import com.speechpro.onepass.framework.media.AudioRecorder;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.util.BitmapUtil;
import com.speechpro.onepass.framework.util.LogUtils;

/**
 * @author volobuev
 * @since 18.02.2016
 */
public abstract class BasePresenter {

    private static final String TAG = BasePresenter.class.getSimpleName();

    protected final BaseActivity mBaseActivity;

    private final IModel model;
    private final AudioHelper mAudioHelper;

    private AudioRecorder mAudioRecorder;


    public BasePresenter(IModel model, BaseActivity baseActivity) {
        this.model = model;
        this.mBaseActivity = baseActivity;
        this.mAudioHelper = new AudioHelper(baseActivity);
    }

    public void pauseOtherActivePlayer() {
        mAudioHelper.pauseOtherActivePlayer();
    }

    public void playOtherActivePlayer() {
        mAudioHelper.playOtherActivePlayer();
    }

    protected IModel getModel() {
        onConnectionFailed();
        return model;
    }

    private void onConnectionFailed() {
        if (!isNetworkOnline()) {
            mBaseActivity.finish();
        }
    }

    private boolean isNetworkOnline() {
        ConnectivityManager connMgr     = (ConnectivityManager) mBaseActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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

    public void cancelRecording() {
        Log.i(TAG, "Recording is canceled.");
        if (mAudioRecorder != null)
            mAudioRecorder.cancel();
    }

    public void stopRecording() {
        Log.i(TAG, "Recording is stopped.");
        if (mAudioRecorder != null)
            mAudioRecorder.stop();
    }

    public void removeAudioListener() {
        Log.i(TAG, "AudioListener remove");
        if (mAudioRecorder != null)
            mAudioRecorder.removeAudioListener();
    }

    public void processDynamicAudio(byte[] pcmBytes) throws CoreException {
        LogUtils.logVoice(pcmBytes);
        addDynamicVoiceSample(pcmBytes, getPassphrase());
    }

    public void processStaticAudio(byte[] pcmBytes) throws CoreException {
        LogUtils.logVoice(pcmBytes);
        addStaticVoiceSample(pcmBytes);
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

        int newWidth = 240;
        int newHeight = 320;

        switch (mBaseActivity.getCameraQuality()) {
            case LOW:
                newWidth = 240;
                newHeight = 320;
                break;
            case MEDIUM:
                newWidth = 480;
                newHeight = 640;
                break;
        }

        byte[] resizedPicture = BitmapUtil.proportionalResizePicture(rotatedData, newWidth);

        LogUtils.logFaces(resizedPicture);
        addFaceSample(resizedPicture);
    }


    /**
     * Receiving session and transaction
     *
     * @throws InternetConnectionException if there is a network error
     */
    public abstract void init() throws InternetConnectionException, RestException;

    /**
     * Return session passphrase or current
     *
     * @return passphrase
     */
    public abstract String getPassphrase();

    public abstract Episode getEpisode();

    public abstract Boolean getResult() throws CoreException;

    public abstract Pair<Boolean, String> getResultWithMessage() throws CoreException;

    public abstract int getRecordingTimeout();

    public abstract void restartTransaction();

    protected abstract void addDynamicVoiceSample(byte[] pcm, String passphrase) throws CoreException;

    protected abstract void addStaticVoiceSample(byte[] pcm) throws CoreException;

    protected abstract void addFaceSample(byte[] face) throws CoreException;

    protected abstract void addVideo(byte[] video) throws CoreException;

    protected abstract void delete() throws CoreException;

}
