package com.speechpro.stcdemo.login;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import com.speechpro.onepass.framework.FrameworkFactory;
import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;
import com.speechpro.stcdemo.common.SharedPref;
import com.speechpro.stcdemo.settings.general.GeneralSettingsActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
public class LoginPresenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginActivity mActivity;
    private BroadcastReceiver mNetworkReceiver;

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler();

    @Inject
    SharedPref mSharedPref;

    public LoginPresenter() {
        STCDemoApp.getInstance().getAppComponent().inject(this);
        mNetworkReceiver = new NetworkStateReceiver(this);
    }

    public void setActivity(LoginActivity activity) {
        this.mActivity = activity;
    }

    void startEnrollment(final String email) {
        mActivity.hideSoftKeyboard();
        mActivity.showProgress();
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                FrameworkFactory.Framework framework = FrameworkFactory.get(
                        mSharedPref.getServerCredentials().serverUrl,
                        mSharedPref.getServerCredentials().sessionUrl,
                        mSharedPref.getServerCredentials().username,
                        mSharedPref.getServerCredentials().password,
                        Integer.parseInt(mSharedPref.getServerCredentials().domainId),
                        mSharedPref.hasDynamicVoice(),
                        mSharedPref.hasStaticVoice(),
                        mSharedPref.hasFace(),
                        mSharedPref.hasLiveness(),
                        mSharedPref.isDebugMode(),
                        mSharedPref.getCameraQuality());
                try {
                    if (framework.isEnrolled(email, true)) {
                        framework.delete(email);
                    }
                    // Beginning of registration
                    framework.startEnrollment(mActivity, email);

                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.showError(mActivity.getString(R.string.network_error));
                            mActivity.hideProgress();
                        }
                    });
                }
            }
        });
    }

    void startVerification(final String email) {
        mActivity.hideSoftKeyboard();
        mActivity.showProgress();
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                FrameworkFactory.Framework framework = FrameworkFactory.get(
                        mSharedPref.getServerCredentials().serverUrl,
                        mSharedPref.getServerCredentials().sessionUrl,
                        mSharedPref.getServerCredentials().username,
                        mSharedPref.getServerCredentials().password,
                        Integer.parseInt(mSharedPref.getServerCredentials().domainId),
                        mSharedPref.hasDynamicVoice(),
                        mSharedPref.hasStaticVoice(),
                        mSharedPref.hasFace(),
                        mSharedPref.hasLiveness(),
                        mSharedPref.isDebugMode(),
                        mSharedPref.getCameraQuality());
                framework.startVerification(mActivity, email);
            }
        });
    }

    void showSettings(String email) {
        mSharedPref.setLogin(email);
        mActivity.startActivity(new Intent(mActivity, GeneralSettingsActivity.class));
    }

    void checkEnroll(final String email) {
        if (isEmptyEmail(email)) {
            mActivity.hideIncorrectEmail();
            return;
        }

        if (!isValidEmail(email)) {
            mActivity.showIncorrectEmail();
            return;
        } else {
            mActivity.hideIncorrectEmail();
        }

        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                FrameworkFactory.Framework framework = FrameworkFactory.get(
                        mSharedPref.getServerCredentials().serverUrl,
                        mSharedPref.getServerCredentials().sessionUrl,
                        mSharedPref.getServerCredentials().username,
                        mSharedPref.getServerCredentials().password,
                        Integer.parseInt(mSharedPref.getServerCredentials().domainId),
                        mSharedPref.hasDynamicVoice(),
                        mSharedPref.hasStaticVoice(),
                        mSharedPref.hasFace(),
                        mSharedPref.hasLiveness(),
                        mSharedPref.isDebugMode(),
                        mSharedPref.getCameraQuality());
                try {
                    boolean isEnrolled = framework.isEnrolled(email, false);

                    if (isEnrolled) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.loginEnabled();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.signUpEnabled();
                            }
                        });
                    }
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.showError(mActivity.getString(R.string.network_error));
                            mActivity.hideProgress();
                        }
                    });
                }
            }
        });

    }

    String getLogin() {
        return mSharedPref.getLogin();
    }

    void setLogin(String login) {
        mSharedPref.setLogin(login);
    }

    void networkConnected() {
        String email = mActivity.mEmailEditText.getText().toString();
        checkEnroll(email);
    }

    void networkDisconnected() {
        mActivity.buttonsDisabled();
    }

    void registerBroadcastReceiver() {
        mActivity.registerReceiver(mNetworkReceiver, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));
    }

    void unregisterBroadcastReceiver() {
        mActivity.unregisterReceiver(mNetworkReceiver);
    }

    private boolean isEmptyEmail(CharSequence target) {
        return TextUtils.isEmpty(target);
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
