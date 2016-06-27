package com.speechpro.onepass.framework.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.injection.components.UIComponent;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.util.NumberMapper;
import com.speechpro.onepass.framework.view.BorderView;
import com.speechpro.onepass.framework.view.activity.BaseActivity;


import javax.inject.Inject;

import static com.speechpro.onepass.framework.util.Constants.SUCCESS_VERIFICATION_FACES;

/**
 * @author volobuev
 * @since 10.06.16
 */
public class VideoBorderFragment extends Fragment implements BorderView {

    private final static Handler UI_HANDLER = new Handler();

    @Inject
    PreviewCallback previewCallback;

    private LinearLayout videoLayout;
    private LinearLayout analyzingLayout;
    private LinearLayout failedLayout;

    private TextView enroll;

    private Button takeButton;
    private Button retakeButton;

    private FragmentShower        shower;
    private VerificationPresenter presenter;
    private BaseActivity          activity;

    private boolean isRecording = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_video_border, container, false);

        this.activity = (BaseActivity) getActivity();
        this.presenter = (VerificationPresenter) activity.getPresenter();
        this.presenter.setBorderView(this);

        this.videoLayout = (LinearLayout) view.findViewById(R.id.video);
        this.analyzingLayout = (LinearLayout) view.findViewById(R.id.analyzing);
        this.failedLayout = (LinearLayout) view.findViewById(R.id.failed);

        this.videoLayout.setVisibility(View.VISIBLE);
        this.analyzingLayout.setVisibility(View.GONE);
        this.failedLayout.setVisibility(View.GONE);

        this.enroll = (TextView) view.findViewById(R.id.text_enroll);
        this.enroll.setVisibility(View.INVISIBLE);

        this.takeButton = (Button) view.findViewById(R.id.take_button);
        this.takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        this.retakeButton = (Button) view.findViewById(R.id.retake_button);
        this.retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retake();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.shower = new FragmentShower(getActivity(), this);
        this.initialize();

        this.previewCallback.setBorderView(this);
    }

    @Override
    public void onDetach() {
        UI_HANDLER.removeCallbacksAndMessages(null);
        super.onDetach();
    }

    @Override
    public void onGreenBorder() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (VideoBorderFragment.this.isAdded()) {

                }
            }
        });
    }


    @Override
    public void onRedBorder() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (VideoBorderFragment.this.isAdded()) {

                }
            }
        });
    }

    @Override
    public void onNormalBorder() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (VideoBorderFragment.this.isAdded()) {

                }
            }
        });
    }

    @Override
    public int successFrames() {
        return SUCCESS_VERIFICATION_FACES;
    }

    private void initialize() {
        shower.getComponent(UIComponent.class).inject(this);
    }

    private void startStop() {

        if (!isRecording) {
            isRecording = true;
            presenter.onStartRecording();
            while (!presenter.isRecPrepared()){

            }
            this.enroll.setText(NumberMapper.convert(presenter.getPassphrase()));
            enroll.setVisibility(View.VISIBLE);
            takeButton.setText(R.string.stop_button);
        } else {
            analyzingLayout.setVisibility(View.VISIBLE);
            isRecording = false;
            presenter.onStopRecordingByButton();
            if (presenter.processVideo()) {
                activity.nextEpisode();
            } else {
                takeButton.setText(R.string.start_button);
                videoLayout.setVisibility(View.GONE);
                analyzingLayout.setVisibility(View.GONE);
                failedLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void retake() {
        presenter.restartSession();
        enroll.setVisibility(View.INVISIBLE);
        videoLayout.setVisibility(View.VISIBLE);
        analyzingLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);
    }

}
