package com.speechpro.stcdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author volobuev
 * @since 29.04.16
 */
public class AboutFragment extends Fragment {

    private Button okButton;
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        okButton = (Button) view.findViewById(R.id.ok_button);

        activity = (MainActivity) getActivity();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.replaceFragment(activity.getMainFragment());
            }
        });

        return view;
    }
}
