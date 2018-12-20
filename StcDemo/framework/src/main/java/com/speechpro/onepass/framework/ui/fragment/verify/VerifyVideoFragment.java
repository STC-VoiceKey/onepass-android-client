package com.speechpro.onepass.framework.ui.fragment.verify;

import android.os.FileObserver;
import android.util.Log;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.ui.listeners.VideoCapturedListener;

import java.io.File;

/**
 * Created by Alexander Grigal on 01.03.18.
 */
public class VerifyVideoFragment extends FaceFragment implements VideoCapturedListener {

    private static final String TAG = VerifyVideoFragment.class.getSimpleName();

    @Override
    public void onResume() {
        super.onResume();
        mCameraSourcePreview.setVideoCapturedListener(this);
    }

    @Override
    protected void startVerification() {
        if (!mIsRecording) {
            mSensorManager.unregisterListener(this);
            mPresenter.pauseOtherActivePlayer();

            mCameraSourcePreview.recordVideo(mActivity.getCameraQuality());

            mCameraSourcePreview.setFaceDetectorListener(null);

            startVerificationCountDownTimer();
        }

    }

    @Override
    public void onVideoCaptured(final String path) {
        FileObserver observer = new FileObserver(path, FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String additionalPath) {
                if (event == FileObserver.CLOSE_WRITE) {
                    if (mIsRecording) {
                        Log.d(TAG, "CAPTURED VIDEO SAVED (CODE: " + event + ") IN " + path);
                        mIsRecording = false;
                        mPresenter.playOtherActivePlayer();
                    } else {
                        boolean delResult = new File(path).delete();
                        Log.d(TAG,
                                (delResult ? "CAPTURED VIDEO REMOVED FROM " : "CAPTURED VIDEO NOT REMOVED FROM ") + path);
                    }
                    stopWatching();
                }
            }
        };
        observer.startWatching();
    }

    @Override
    protected void process() {
        Log.d(TAG, "processVideo: ");
        if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
            loading(true);
            mService.execute(new Runnable() {
                @Override
                public void run() {
                    if (mCameraSourcePreview != null && mCameraSourcePreview.isRecordingVideo()) {
                        mCameraSourcePreview.stopVideo();

                        try {
//                            mCameraView.tryToStop();
                        } catch (RuntimeException e) {
                            Log.e(TAG, "CAMERA TRY TO STOP: " + e.getMessage());
                        }
                        Log.d(TAG, "VIDEO RECORDING STOPPED");

                        try {
                            Log.d(TAG, "Processing video...");
//                            mPresenter.processVideo(mCameraView.getVideo());
                            mPresenter.processVideo(mCameraSourcePreview.getVideo());
                            mPresenter.getResultWithMessage();
                            Log.d(TAG, "Ready to go to next episode.");
                            mActivity.nextEpisode();
                        } catch (CoreException ex) {
                            Log.d(TAG, "CoreException");
                            mPresenter.restartTransaction();
                            loading(false);
                            final String descriptionError = ((RestException) ex).reason;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                                        showError(descriptionError);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
