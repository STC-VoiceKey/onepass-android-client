package com.speechpro.onepass.framework.view.fragment;


import android.os.Bundle;
import android.widget.Toast;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.view.MediaView;

/**
 * @author volobuev
 * @since 14.06.16
 */
public class VideoFragment extends VisionFragment implements MediaView {

    private VerificationPresenter presenter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter = (VerificationPresenter) activity.getPresenter();
        presenter.setMediaView(this);
        presenter.setVisionView(this);
    }

    @Override
    public void processVision() {
        start();
    }

    @Override
    public void start() {
        presenter.onStartRecording();
    }

    @Override
    public void stop() {
        if (isVisible()) {
            boolean isDone = presenter.processVideo();
            if (isDone) {
                activity.nextEpisode();
            }else {
                presenter.getBorderView().onRedBorder();
            }
        }
    }

    @Override
    public void toast(int resId) {

    }
}
