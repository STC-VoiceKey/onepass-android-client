package com.speechpro.onepass.framework.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.util.Network;

/**
 * @author volobuev
 * @since 07.10.16
 */
public class AgreementFragment extends BaseFragment {

    private Button agreeButton;
    private Button disagreeButton;
    private ProgressBar progressBar;
    private View linearLayout;
    private View linearLayout2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_agreement, container, false);

        final BaseActivity baseActivity = (BaseActivity) getActivity();

        linearLayout = view.findViewById(R.id.linearLayout);
        linearLayout2 = view.findViewById(R.id.linearLayout2);

        agreeButton = (Button) view.findViewById(R.id.continue_button);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showProgress(true);
                if (!Network.isAvailable(baseActivity)) {
                    baseActivity.finish();
                    return;
                }
                baseActivity.nextEpisode();
            }
        });

        disagreeButton = (Button) view.findViewById(R.id.cancel_button);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseActivity.finish();
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showProgress(boolean isShow) {
        linearLayout.setVisibility(isShow ? View.GONE : View.VISIBLE);
        linearLayout2.setVisibility(isShow ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


}
