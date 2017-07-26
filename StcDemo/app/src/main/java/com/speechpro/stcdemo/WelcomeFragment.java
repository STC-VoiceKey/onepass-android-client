package com.speechpro.stcdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author volobuev
 * @since 07.10.16
 */
public class WelcomeFragment extends Fragment {

    //After that timeout LoginFragment will run
    private final static int START_TIMEOUT = 500;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_welcome, container, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.replaceFragment(mainActivity.getLoginFragment());
                }
            }
        }, START_TIMEOUT);

        return view;
    }
}
