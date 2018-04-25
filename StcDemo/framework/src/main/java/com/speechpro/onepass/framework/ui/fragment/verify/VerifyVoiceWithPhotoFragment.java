package com.speechpro.onepass.framework.ui.fragment.verify;

import android.util.Log;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.media.AudioListener;
import com.speechpro.onepass.framework.ui.listeners.PictureCapturedListener;

/**
 * Created by Alexander Grigal on 01.03.18.
 */
public class VerifyVoiceWithPhotoFragment extends FaceFragment
        implements PictureCapturedListener, AudioListener {

    private static final String TAG = VerifyVideoFragment.class.getSimpleName();

    private byte[] voice;

    @Override
    public void onResume() {
        super.onResume();
        mCameraSourcePreview.setPictureCapturedListener(this);
    }

    @Override
    protected void startVerification() {
        if (!mIsRecording) {
            mPresenter.pauseOtherActivePlayer();
            mPresenter.startRecording(this);

            mCameraSourcePreview.setFaceDetectorListener(null);

            startVerificationCountDownTimer();
        }
    }

    @Override
    public void onPictureCaptured(final byte[] picture, final int degrees) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCameraSourcePreview.stop();
            }
        });

        mService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mPresenter.processPhoto(picture, degrees);
                    mPresenter.processAudio(voice);
                    mPresenter.getResultWithMessage();
                    mActivity.nextEpisode();
                } catch (CoreException ex) {
                    mPresenter.restartTransaction();

                    final String descriptionError = ((RestException) ex).reason;

                    if (mActivity != null && !mActivity.isFinishing()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                loading(false);
                                showError(descriptionError);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void process() {
        Log.d(TAG, "processVideo: ");
        if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
            mPresenter.stopRecording();
            mCameraSourcePreview.captureImage();
            loading(true);
        }

    }

    @Override
    public void start() {

    }

    @Override
    public void stop(byte[] result) {
        this.voice = result;
    }

    @Override
    public void onProcess(short amplitude) {

    }
}
