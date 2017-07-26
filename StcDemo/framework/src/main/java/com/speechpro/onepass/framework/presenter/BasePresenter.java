package com.speechpro.onepass.framework.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.media.AudioHelper;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;

/**
 * @author volobuev
 * @since 18.02.2016
 */
public abstract class BasePresenter {

    private static final String TAG = "BasePresenter";
    protected final BaseActivity mBaseActivity;

    private final IModel model;
    private final AudioHelper mAudioHelper;


    public BasePresenter(IModel model, BaseActivity mBaseActivity) {
        this.model = model;
        this.mBaseActivity = mBaseActivity;
        this.mAudioHelper = new AudioHelper(mBaseActivity);
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

    /**
     * Return session passphrase or current
     *
     * @return passphrase
     */
    public abstract String getPassphrase();

    public abstract Episode getEpisode();

    public abstract boolean getResult() throws CoreException;

    public abstract int getRecordingTimeout();

    public abstract void restartSession();

    protected abstract void addVoiceSample(byte[] pcm, String passphrase) throws CoreException;

    protected abstract void addFaceSample(byte[] face) throws CoreException;

    protected abstract void addVideo(byte[] video) throws CoreException;

    protected abstract void delete() throws CoreException;

}
