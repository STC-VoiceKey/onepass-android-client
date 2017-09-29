package com.speechpro.stcdemo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.speechpro.stcdemo.util.AppInfo;
import com.speechpro.stcdemo.util.FullscreenBugWorkaround;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by grigal on 03.05.2017.
 */

public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private MainActivity mMainActivity;
    private View mProgressBar;
    private TextView mTitleTextView;
    private EditText mUriEditText;
    private Button mSaveButton;
    private Button mDeleteUserButton;
    private Button mDefaultSettingsButton;

    private ExecutorService mService = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler();

    private String initialValue;

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.f_settings, container, false);

        mMainActivity = (MainActivity) getActivity();
        mProgressBar = view.findViewById(R.id.progressbar);
        mTitleTextView = (TextView) view.findViewById(R.id.title);
        mUriEditText = (EditText) view.findViewById(R.id.url);
        mSaveButton = (Button) view.findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.hideSoftKeyboard(v);
                if (!isValidUrl()) {
                    actionSnackbar(getView(), R.string.not_valid_url);
                    return;
                }

                if (mUriEditText.getText().toString().isEmpty()) {
                    mMainActivity.putPref(getString(R.string.base_url_pref), getString(R.string.url));
                } else {
                    mMainActivity.putPref(getString(R.string.base_url_pref), mUriEditText.getEditableText().toString());
                }
                mMainActivity.replaceFragment(mMainActivity.getLoginFragment());
            }
        });
        mDeleteUserButton = (Button) view.findViewById(R.id.delete_user);
        mDeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.hideSoftKeyboard(view);
                showRemoveUserDialog(mMainActivity.getPref(getString(R.string.email_pref)));
            }
        });
        mDefaultSettingsButton = (Button) view.findViewById(R.id.default_settings);
        mDefaultSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.hideSoftKeyboard(view);
                showDefaultSettingsDialog();
            }
        });
        mTitleTextView.setText(String.format(getString(R.string.online_version),
                AppInfo.getVersionName(mMainActivity),
                AppInfo.getVersionCode(mMainActivity)));
        mUriEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mUriEditText.hasFocus()) {
                    if (s.toString().equals(initialValue)) {
                        mSaveButton.setEnabled(false);
                    } else {
                        mSaveButton.setEnabled(true);
                    }
                }
            }
        });

        mMainActivity.onTouchListener(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FullscreenBugWorkaround.getInstance(getActivity()).setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        initialValue = mMainActivity.getPref(getString(R.string.base_url_pref), getString(R.string.url));
        mUriEditText.setText(initialValue);
    }

    @Override
    public void onStop() {
        super.onStop();
        FullscreenBugWorkaround.getInstance(getActivity()).removeListener();
    }

    private void showProgress(boolean isShowed) {
        mTitleTextView.setVisibility(isShowed ? View.GONE : View.VISIBLE);
        mUriEditText.setVisibility(isShowed ? View.GONE : View.VISIBLE);
        mSaveButton.setVisibility(isShowed ? View.GONE : View.VISIBLE);
        mDeleteUserButton.setVisibility(isShowed ? View.GONE : View.VISIBLE);
        mDefaultSettingsButton.setVisibility(isShowed ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isShowed ? View.VISIBLE : View.GONE);
    }

    private void showRemoveUserDialog(final String email) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(mMainActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.remove_user);
        builder.setMessage(String.format(getString(R.string.are_you_sure_you_want_to_remove_user),
                email != null ? email : ""));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (email != null && !email.isEmpty()) {
                    deleteUser();
                } else {
                    actionSnackbar(getView(), R.string.text_no_account);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void showDefaultSettingsDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(mMainActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.default_settings));
        builder.setMessage(getString(R.string.restore_settings));
        builder.setPositiveButton(getString(R.string.restore), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMainActivity.putPref(getString(R.string.base_url_pref), getString(R.string.url));
                mMainActivity.replaceFragment(mMainActivity.getLoginFragment());
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void deleteUser() {
        showProgress(true);
        final String email = mMainActivity.getPref(getString(R.string.email_pref));

        mService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean hasDeleted = mMainActivity.getFramework().delete(email);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (hasDeleted) {
                                mDeleteUserButton.setEnabled(false);
                                mDefaultSettingsButton.setEnabled(false);
                                mMainActivity.putPref(getString(R.string.email_pref), "");
                                actionSnackbar(getView(), R.string.text_user_removed);
                                mMainActivity.replaceFragment(mMainActivity.getLoginFragment());
                            } else {
                                actionSnackbar(getView(), R.string.text_no_account);
                            }
                            showProgress(false);
                        }
                    });
                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            actionSnackbar(getView(), R.string.network_error);
                            showProgress(false);
                        }
                    });
                    Log.e(TAG, "Connection failed", e);
                }
            }
        });
    }

    private void actionSnackbar(View view, int resId) {
        Snackbar.make(view, getString(resId), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private boolean isValidUrl() {
        String url = mUriEditText.getText().toString().toLowerCase();
        return (url.startsWith("http://") || url.startsWith("https://"))
                && Patterns.WEB_URL.matcher(url).matches();
    }
}
