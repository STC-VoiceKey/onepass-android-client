package com.speechpro.onepass.framework.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.injection.components.UIComponent;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.view.BorderView;
import com.speechpro.onepass.framework.view.activity.EnrollmentActivity;

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

    private LinearLayout closedEyesLayout;
    private LinearLayout manyFacesLayout;
    private LinearLayout sunglassesLayout;
    private LinearLayout poorQualityLayout;
    private LinearLayout faceNotFoundLayout;

    private ImageButton takeButton;
    private Button retakeButton;

    private FragmentShower shower;
    private EnrollmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_camera_border, container, false);
        activity = (EnrollmentActivity) getActivity();

        photoLayout = (LinearLayout) view.findViewById(R.id.photo);
        analyzingLayout = (LinearLayout) view.findViewById(R.id.analyzing);

        failedLayout = (LinearLayout) view.findViewById(R.id.failed);
        closedEyesLayout = (LinearLayout) view.findViewById(R.id.closed_eyes_layout);
        manyFacesLayout = (LinearLayout) view.findViewById(R.id.many_faces_layout);
        sunglassesLayout = (LinearLayout) view.findViewById(R.id.sunglasses_layout);
        poorQualityLayout = (LinearLayout) view.findViewById(R.id.poor_quality_layout);
        faceNotFoundLayout = (LinearLayout) view.findViewById(R.id.face_not_found_layout);

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

        try {
            ((EnrollmentPresenter) activity.getPresenter()).processPhoto();
            previewCallback.onDetach();
            activity.nextEpisode();
        } catch (CoreException ex) {
            RestException restException = (RestException) ex;
            photoLayout.setVisibility(View.GONE);
            analyzingLayout.setVisibility(View.GONE);
            failedLayout.setVisibility(View.VISIBLE);
            parseReason(restException.reason);
        }
    }

    private void retake() {
        photoLayout.setVisibility(View.VISIBLE);
        analyzingLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);
    }

    private void parseReason(String reason) {
        if (reason.contains("face not found in image")) {
            closedEyesLayout.setVisibility(View.GONE);
            manyFacesLayout.setVisibility(View.GONE);
            sunglassesLayout.setVisibility(View.GONE);
            poorQualityLayout.setVisibility(View.GONE);
            faceNotFoundLayout.setVisibility(View.VISIBLE);
        } else if (reason.contains("multiple faces found in image")) {
            closedEyesLayout.setVisibility(View.GONE);
            manyFacesLayout.setVisibility(View.VISIBLE);
            sunglassesLayout.setVisibility(View.GONE);
            poorQualityLayout.setVisibility(View.GONE);
            faceNotFoundLayout.setVisibility(View.GONE);
        } else {

        }
    }

}
