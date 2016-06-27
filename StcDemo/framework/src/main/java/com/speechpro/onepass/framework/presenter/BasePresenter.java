package com.speechpro.onepass.framework.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.media.Recorder;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.presenter.episode.Episode;
import com.speechpro.onepass.framework.view.BorderView;
import com.speechpro.onepass.framework.view.MediaView;
import com.speechpro.onepass.framework.view.VisionView;


/**
 * @author volobuev
 * @since 18.02.2016
 */
public abstract class BasePresenter {

    private static final String TAG = "BasePresenter";

    private static boolean isRecording = false;
    protected final PreviewCallback previewCallback;
    protected final Context         context;
    private final   IModel          model;
    protected       Recorder        recorder;
    protected       VisionView      visionView;
    private         MediaView       mediaView;
    private         BorderView      borderView;

    private final Handler recordingHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            if (isRecording) {
                onStopRecording();
                mediaView.stop();
            }
        }
    };

    public BasePresenter(IModel model, PreviewCallback previewCallback, Context context) {
        this.model = model;
        this.previewCallback = previewCallback;
        this.context = context;
    }

    public void onStopRecordingByButton() {
        recordingHandler.removeMessages(0);
        onStopRecording();
    }

    public void setMediaView(MediaView mediaView) {
        this.mediaView = mediaView;
    }

    public void setVisionView(VisionView visionView) {
        this.visionView = visionView;
    }

    public void setBorderView(BorderView borderView){
        this.borderView = borderView;
    }

    public BorderView getBorderView() {
        return borderView;
    }

    public void onStartRecording() {
        Log.i(TAG, "Recording is started...");
        isRecording = true;
        recorder.startRecording();
        recordingHandler.sendEmptyMessageDelayed(0, getRecordingTimeout());
    }

    public void onStopRecording() {
        Log.i(TAG, "Recording is stopped...");
        isRecording = false;
        recorder.stopRecording();
    }

    public synchronized void stopAtAllRecording() {
        Log.d(TAG, "Stop all recording");
        if (isRecording) {
            isRecording = false;
            recorder.stopRecording();
            recordingHandler.removeCallbacksAndMessages(null);
        }
    }

    protected void toast(int resId) {
        mediaView.toast(resId);
        stopAtAllRecording();
    }

    protected IModel getModel() {
        return model;
    }

    public abstract String getPassphrase();

    public abstract Episode getEpisode();

    public abstract boolean getResult();

    protected abstract long getRecordingTimeout();

    protected abstract void addVoiceSample(byte[] pcm, String passphrase) throws CoreException;

    protected abstract void addFaceSample(byte[] face) throws CoreException;

    protected abstract void addVideo(byte[] video) throws CoreException;

    protected abstract void delete() throws CoreException;

}
