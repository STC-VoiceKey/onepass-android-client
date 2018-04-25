package com.speechpro.onepass.framework.ui.fragment.enroll;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.fragment.BaseFragment;
import com.speechpro.onepass.framework.util.Constants;

import static android.app.Activity.RESULT_OK;
import static com.speechpro.onepass.framework.util.Constants.ACTIVITY_RESULT;
import static com.speechpro.onepass.framework.util.Constants.SUCCES;

/**
 * @author volobuev
 * @since 25.06.16
 */
public class EnrollResultFragment extends BaseFragment {

    private EnrollmentActivity mActivity;
    private Button mComplete;

    private Handler mHandler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_reg_result, container, false);
        mActivity = (EnrollmentActivity) getActivity();
        mComplete = (Button) view.findViewById(R.id.complete);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishActivity();
            }
        }, Constants.RESULT_DELAY);

        return view;
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra(ACTIVITY_RESULT, SUCCES);
        mActivity.setResult(RESULT_OK, intent);
        mActivity.finish();
    }
}
