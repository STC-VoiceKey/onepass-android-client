package com.speechpro.onepass.framework.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.injection.components.UIComponent;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.view.BorderView;
import com.speechpro.onepass.framework.view.activity.BaseActivity;
import com.speechpro.onepass.framework.view.activity.EnrollmentActivity;
import pl.droidsonroids.gif.GifImageView;

import static com.speechpro.onepass.framework.R.drawable.border_green;
import static com.speechpro.onepass.framework.R.drawable.border;
import static com.speechpro.onepass.framework.R.drawable.border_red;
import static com.speechpro.onepass.framework.util.Constants.SUCCESS_FACES;

import javax.inject.Inject;

/**
 * @author volobuev
 * @since 28.03.16
 */
public class FaceBorderFragment extends Fragment implements BorderView {

    private final static Handler UI_HANDLER = new Handler();

    @Inject
    PreviewCallback previewCallback;

    private LinearLayout photoLayout;
    private LinearLayout analyzingLayout;
    private LinearLayout failedLayout;

    private ImageButton takeButton;
    private Button      retakeButton;

    private FragmentShower     shower;
    private EnrollmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_camera_border, container, false);
        activity = (EnrollmentActivity) getActivity();

        photoLayout = (LinearLayout) view.findViewById(R.id.photo);
        analyzingLayout = (LinearLayout) view.findViewById(R.id.analyzing);
        failedLayout = (LinearLayout) view.findViewById(R.id.failed);

        photoLayout.setVisibility(View.VISIBLE);
        analyzingLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);


        takeButton = (ImageButton) view.findViewById(R.id.take_button);
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeFace();
            }
        });

        retakeButton = (Button) view.findViewById(R.id.retake_button);
        retakeButton.setOnClickListener(new View.OnClickListener() {
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

    public void initialize() {
        shower.getComponent(UIComponent.class).inject(this);
    }

    @Override
    public void onGreenBorder() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (FaceBorderFragment.this.isAdded()) {

                }
            }
        });
    }

    @Override
    public void onRedBorder() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (FaceBorderFragment.this.isAdded()) {

                }
            }
        });
    }

    @Override
    public void onNormalBorder() {
        UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (FaceBorderFragment.this.isAdded()) {

                }
            }
        });
    }

    @Override
    public int successFrames() {
        return SUCCESS_FACES;
    }

    private void takeFace() {
        analyzingLayout.setVisibility(View.VISIBLE);
        failedLayout.setVisibility(View.GONE);

        if (((EnrollmentPresenter) activity.getPresenter()).processPhoto()) {
            previewCallback.onDetach();
            activity.nextEpisode();
        } else {
            photoLayout.setVisibility(View.GONE);
            analyzingLayout.setVisibility(View.GONE);
            failedLayout.setVisibility(View.VISIBLE);
        }
    }

    private void retake() {
        photoLayout.setVisibility(View.VISIBLE);
        analyzingLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);
    }

}
