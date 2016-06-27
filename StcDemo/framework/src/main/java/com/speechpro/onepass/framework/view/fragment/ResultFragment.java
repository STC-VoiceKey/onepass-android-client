package com.speechpro.onepass.framework.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.BasePresenter;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.util.Constants;
import com.speechpro.onepass.framework.view.activity.BaseActivity;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author volobuev
 * @since 27.04.16
 */
public class ResultFragment extends BaseFragment {

    private RelativeLayout resultLayout;
    private RelativeLayout grantedLayout;
    private RelativeLayout deniedLayout;

    private BaseActivity  activity;
    private BasePresenter presenter;

    private Handler handler      = new Handler();
    private boolean isEnrollment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_result, container, false);

        activity = (BaseActivity) getActivity();
        presenter = activity.getPresenter();

        resultLayout = (RelativeLayout) view.findViewById(R.id.result_layout);
        grantedLayout = (RelativeLayout) view.findViewById(R.id.granted);
        deniedLayout = (RelativeLayout) view.findViewById(R.id.denied);

        initialize();

        return view;
    }

    public void initialize() {
        isEnrollment = presenter instanceof EnrollmentPresenter;
        if (presenter.getResult()) {
            grantedLayout.setVisibility(View.VISIBLE);
            deniedLayout.setVisibility(View.GONE);
        } else {
            grantedLayout.setVisibility(View.GONE);
            deniedLayout.setVisibility(View.VISIBLE);
        }

        resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.nextEpisode();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.nextEpisode();
            }
        }, Constants.RESULT_DELAY);
    }

    @Override
    protected boolean isEnrollment() {
        return isEnrollment;
    }
}
