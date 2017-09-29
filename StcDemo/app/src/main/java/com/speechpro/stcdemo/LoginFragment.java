package com.speechpro.stcdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.speechpro.onepass.framework.Framework;
import com.speechpro.onepass.framework.permissions.IRequestPermissionListener;
import com.speechpro.onepass.framework.permissions.RequestPermissions;
import com.speechpro.onepass.framework.util.AppUtils;
import com.speechpro.stcdemo.util.FullscreenBugWorkaround;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author volobuev
 * @since 07.10.16
 */
public class LoginFragment extends EnterFragment {

    private final static String TAG = LoginFragment.class.getName();

    private View mProgressBar;
    private View mMain;

    private Handler mHandler = new Handler();
    private ExecutorService mService = Executors.newSingleThreadExecutor();

    private BroadcastReceiver br;

    private Timer timer = new Timer();
    private final int DELAY = 500;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.f_login, container, false);

        mMainActivity = (MainActivity) getActivity();

        layout_warning = view.findViewById(R.id.layout_warning);
        warning = (TextView) view.findViewById(R.id.text_warning);
        emailEdit = (EditText) view.findViewById(R.id.email);
        logo = (ImageView) view.findViewById(R.id.iv_logo);
        settings = (ImageView) view.findViewById(R.id.iv_settings);
        mProgressBar = view.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mMain = view.findViewById(R.id.main);

        final String email = ((MainActivity) getActivity()).getPref(getString(R.string.email_pref), "");
        if (!email.isEmpty()) {
            emailEdit.setText(email);
        }

        loginButton = (Button) view.findViewById(R.id.login);
        enrollButton = (Button) view.findViewById(R.id.signup);

        layout_warning.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.hideSoftKeyboard(view);
                showProgress(true);
                if (AppUtils.hasRuntimePermissions()) {
                    RequestPermissions.checkAllPermissions(new IRequestPermissionListener() {
                        @Override
                        public void result(boolean granted) {
                            if (granted) {
                                mMainActivity.putPref(getString(R.string.email_pref), emailEdit.getText().toString());
                                login();
                            } else {
                                showProgress(false);
                            }
                        }
                    }, mMainActivity);
                } else {
                    mMainActivity.putPref(getString(R.string.email_pref), emailEdit.getText().toString());
                    login();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.hideSoftKeyboard(view);
                mMainActivity.replaceFragment(mMainActivity.getSettingsFragment());
            }
        });

        if (emailEdit.getText().toString().length() <= 0) {
            loginButton.setEnabled(false);
            enrollButton.setEnabled(false);
        }

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.hideSoftKeyboard(view);
                showProgress(true);
                if (AppUtils.hasRuntimePermissions()) {
                    RequestPermissions.checkAllPermissions(new IRequestPermissionListener() {
                        @Override
                        public void result(boolean granted) {
                            if (granted) {
                                mMainActivity.putPref(getString(R.string.email_pref), emailEdit.getText().toString());
                                signup();
                            } else {
                                showProgress(false);
                            }
                        }
                    }, mMainActivity);
                } else {
                    mMainActivity.putPref(getString(R.string.email_pref), emailEdit.getText().toString());
                    signup();
                }
            }
        });

        emailEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                timer.cancel();
                if (emailEdit.hasFocus()) {
                    if (isValidEmail(s.toString())) {
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        checkTextField(s);
                                    }
                                },
                                DELAY
                        );
                    } else {
                        showWarning(s.length() != 0);
                        loginButton.setEnabled(false);
                        enrollButton.setEnabled(false);
                    }
                }
            }
        });

        url = ((MainActivity) getActivity()).getPref(getString(R.string.base_url_pref), getString(R.string.url));
        mMainActivity.setFramework(Framework.getFramework(url));

        mMainActivity.onTouchListener(view);

        return view;
    }

    private void checkTextField(final Editable s) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                loginButton.setEnabled(false);
                enrollButton.setEnabled(false);

                if (s.length() > 0) {
                    if (isValidEmail(s.toString())) {
                        showWarning(false);
                        mService.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final boolean isEnrolled = mMainActivity.getFramework().isEnrolled(s.toString());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isAdded()) return;
                                            if (isValidEmail(emailEdit.getText().toString())) {
                                                loginButton.setEnabled(isEnrolled);
                                                enrollButton.setEnabled(!isEnrolled);
                                                mMainActivity.putPref(getString(R.string.email_pref), s.toString());
                                            } else {
                                                showWarning(!emailEdit.getText().toString().isEmpty());
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "Connection failed", e);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            loginButton.setEnabled(false);
                                            enrollButton.setEnabled(false);
                                            toast(R.string.network_error);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        showWarning(true);
                        loginButton.setEnabled(false);
                        enrollButton.setEnabled(false);
                    }
                } else {
                    showWarning(false);
                    loginButton.setEnabled(false);
                    enrollButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FullscreenBugWorkaround.getInstance(getActivity()).setListener();
        checkInternetConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress(false);
        checkTextField(emailEdit.getText());
    }

    @Override
    public void onPause() {
        super.onPause();
        loginButton.setEnabled(false);
        enrollButton.setEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        FullscreenBugWorkaround.getInstance(getActivity()).removeListener();
        mMainActivity.unregisterReceiver(br);
    }

    private void login() {
        final String email = emailEdit.getText().toString();
        if (isValidEmail(email)) {
            showProgress(true);
            mService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        final boolean isEnrolled = mMainActivity.getFramework().isEnrolled(email);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isEnrolled) {
                                    layout_warning.setVisibility(View.INVISIBLE);
                                    mMainActivity.isEnrollment = false;
                                    mMainActivity.process();
//                                    mMainActivity.getPermissions();
                                } else {
                                    showProgress(false);
                                    layout_warning.setVisibility(View.VISIBLE);
                                    warning.setText(getString(R.string.text_no_account));
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Connection failed", e);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                                toast(R.string.network_error);
                            }
                        });
                    }
                }
            });
        }
    }

    private void signup() {
        final String email = emailEdit.getText().toString();
        if (isValidEmail(email)) {
            showProgress(true);
            mService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        final boolean isEnrolled = mMainActivity.getFramework().isEnrolled(email);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isEnrolled) {
                                    layout_warning.setVisibility(View.INVISIBLE);
                                    mMainActivity.isEnrollment = true;
                                    mMainActivity.process();
                                } else {
                                    showProgress(false);
                                    layout_warning.setVisibility(View.VISIBLE);
                                    warning.setText(getString(R.string.text_account_exists));
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Connection failed", e);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                                toast(R.string.network_error);
                            }
                        });
                    }
                }
            });
        }
    }

    private void showProgress(boolean showed) {
        mMain.setVisibility(showed ? View.GONE : View.VISIBLE);
        logo.setVisibility(showed ? View.GONE : View.VISIBLE);
        settings.setVisibility(showed ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(showed ? View.VISIBLE : View.GONE);
    }

    private void checkInternetConnection() {
        if (br == null) {
            br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    Log.d("TEST Internet", info.toString() + " " + state.toString());

                    if (state == NetworkInfo.State.CONNECTED) {
//                        Toast.makeText(mMainActivity.getApplicationContext(), "Internet connection is on", Toast.LENGTH_LONG).show();
                        checkTextField(emailEdit.getText());
                    } else {
//                        Toast.makeText(mMainActivity.getApplicationContext(), "Internet connection is Off", Toast.LENGTH_LONG).show();
                        loginButton.setEnabled(false);
                        enrollButton.setEnabled(false);
                    }
                }
            };
        }

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mMainActivity.registerReceiver((BroadcastReceiver) br, intentFilter);
    }

}
