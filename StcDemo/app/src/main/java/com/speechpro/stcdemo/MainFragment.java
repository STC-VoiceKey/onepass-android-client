package com.speechpro.stcdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.speechpro.onepass.framework.Framework;

import static android.Manifest.permission.*;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.speechpro.onepass.framework.util.Constants.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends BaseFragment {

    private final static String TAG = "MainFragment";

    private MainActivity      activity;
    private SharedPreferences pref;

    private EditText emailEdit;
    private TextView warning;
    private Button   process;
    private TextView signup;
    private View     fragment;
    private String   url;

    private Button       agreeButton;
    private Button       disagreeButton;

    private LinearLayout mainLayout;
    private LinearLayout infoLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_main, container, false);

        activity = (MainActivity) getActivity();
        pref = activity.getSharedPref();

        agreeButton = (Button) fragment.findViewById(R.id.agree_button);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agree();
            }
        });

        disagreeButton = (Button) fragment.findViewById(R.id.disagree_button);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disagree();
            }
        });

        mainLayout = (LinearLayout) fragment.findViewById(R.id.main);
        mainLayout.setVisibility(View.VISIBLE);

        infoLayout = (LinearLayout) fragment.findViewById(R.id.info);
        infoLayout.setVisibility(View.GONE);

        emailEdit = (EditText) fragment.findViewById(R.id.email);
        warning = (TextView) fragment.findViewById(R.id.warning);
        signup = (TextView) fragment.findViewById(R.id.singup);
        process = (Button) fragment.findViewById(R.id.process);
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });


        url = pref.getString(getString(R.string.url_pref), getString(R.string.url));
        activity.framework = Framework.getFramework(url);

        onTouchListener(fragment);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkOnline()) {
            toast(R.string.network_error);
        }
        mainLayout.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);
        checkPermission();
    }

    private void signin() {
        sing(false, R.string.not_enrolled);
    }

    private void signup() {
        sing(true, R.string.enrolled);
    }

    private void sing(boolean isEnrollment, int toastId) {
        activity.email = emailEdit.getText().toString();
        if (checkEmail(activity.email)) {
            try {
                if (!(isEnrollment == activity.framework.isEnrolled(activity.email))) {
                    warning.setVisibility(View.INVISIBLE);
                    activity.isEnrollment = isEnrollment;
                    infoLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                } else {
                    toast(toastId);
                }
            } catch (Exception e) {
                process.setEnabled(false);
                toast(R.string.network_error);
                Log.e(TAG, "Connection failed", e);
            }
        }
    }

    private boolean checkEmail(String email) {
        if (email != null && !email.isEmpty()) {
            if (isEmailValid(email)) {
                return true;
            } else {
                warning.setText(getString(R.string.email_not_valid));
                warning.setVisibility(View.VISIBLE);
            }
        } else {
            warning.setText(getString(R.string.email_warning));
            warning.setVisibility(View.VISIBLE);
        }
        return false;
    }

    private boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm      = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo         netInfo = cm.getNetworkInfo(TYPE_MOBILE);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(TYPE_WIFI);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Connection failed", e);
            return false;
        }
        return status;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(activity, CAMERA) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(activity, RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                                              new String[]{CAMERA, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                              PERMISSIONS);
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void toast(int msgRes) {
        activity.actionSnackbar(fragment, msgRes);
    }

    private void agree(){
        if(!activity.isEnrollment) {
            activity.framework.startVerification(activity, activity.email);
        } else {
            activity.framework.startEnrollment(activity, activity.email);
        }
    }

    private void disagree() {
        infoLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }
}
