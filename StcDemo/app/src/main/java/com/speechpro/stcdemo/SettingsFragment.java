package com.speechpro.stcdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author volobuev
 * @since 14.04.16
 */
public class SettingsFragment extends BaseFragment {

    private final static String TAG = "SettingsFragment";

    private MainActivity      activity;
    private EditText          urlLink;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        activity = (MainActivity) getActivity();
        pref = activity.getSharedPref();
        urlLink = (EditText) view.findViewById(R.id.url_link);

        Button okButton = (Button) view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok(v);
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


        setPref();
        onTouchListener(view);
        return view;
    }

    private void ok(View view) {
        String url  = urlLink.getText().toString();
        Log.d(TAG, "URL: " + url);
        if (checkUrl(url)) {
            activity.actionSnackbar(view, R.string.url_invalid);
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(getString(R.string.url_pref), url);
            editor.commit();
            back();
        }
    }

    private void back() {
        activity.replaceFragment(activity.getMainFragment());
    }

    private void setPref() {
        String urlPref    = pref.getString(getString(R.string.url_pref), null);
        if (urlPref != null && !urlPref.isEmpty()) {
            urlLink.setText(urlPref);
        }
    }

    private boolean checkUrl(String url) {
        return url == null || url.isEmpty() || url.split("\\s+").length > 1;
    }
}
