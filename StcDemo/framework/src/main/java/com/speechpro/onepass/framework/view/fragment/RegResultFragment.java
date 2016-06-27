package com.speechpro.onepass.framework.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.view.activity.EnrollmentActivity;

/**
 * @author volobuev
 * @since 25.06.16
 */
public class RegResultFragment extends BaseFragment {

    private EnrollmentActivity activity;

    private Button doneButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_reg_result, container, false);
        activity = (EnrollmentActivity) getActivity();

        doneButton = (Button) view.findViewById(R.id.reg_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        return view;
    }

    @Override
    protected boolean isEnrollment() {
        return true;
    }
}
