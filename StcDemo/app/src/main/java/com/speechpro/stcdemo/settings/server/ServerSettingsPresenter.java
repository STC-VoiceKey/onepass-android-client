package com.speechpro.stcdemo.settings.server;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.NotFoundException;
import com.speechpro.onepass.core.exception.RestException;
import com.speechpro.onepass.core.exception.ServiceUnavailableException;
import com.speechpro.onepass.framework.Framework;
import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;
import com.speechpro.stcdemo.common.Cryptographer;
import com.speechpro.stcdemo.common.SharedPref;

import javax.inject.Inject;

/**
 * Created by Alexander Grigal on 06.02.18.
 */
public class ServerSettingsPresenter {

    private static final String TAG = ServerSettingsPresenter.class.getSimpleName();

    private ServerSettingsActivity mActivity;

    @Inject
    SharedPref mSharedPref;

    public ServerSettingsPresenter() {
        STCDemoApp.getInstance().getAppComponent().inject(this);
    }

    public void setActivity(ServerSettingsActivity activity) {
        this.mActivity = activity;
    }

    String getUrl() {
        return mSharedPref.getServerCredentials().url;
    }

    String getUsername() {
        return mSharedPref.getServerCredentials().username;
    }

    String getPassword() {
        return mSharedPref.getServerCredentials().password;
    }

    String getDomainId() {
        return String.valueOf(mSharedPref.getServerCredentials().domainId);
    }

    void checkChangedCredentials(String url, String username, String password, String domainId) {
        if (!mSharedPref.getServerCredentials().equals(new ServerSettingsCredentials(url, username, password, domainId))) {
            mActivity.saveButtonEnabled();
        } else {
            mActivity.saveButtonDisabled();
        }
    }

    void checkDefaultCredentials(String url, String username, String password, String domainId) {
        ServerSettingsCredentials defaultCredentials = new ServerSettingsCredentials(mActivity.getString(R.string.url_vkopdm),
                mActivity.getString(R.string.username), mActivity.getString(R.string.password),
                mActivity.getString(R.string.domain_id));
        if (defaultCredentials.equals(new ServerSettingsCredentials(url, username, password, domainId))) {
            mActivity.defaultButtonDisabled();
        } else {
            mActivity.defaultButtonEnabled();
        }
    }

    void checkValidCredentials(String url, String username, String password, int domainId) {

        mActivity.showProgress();

        boolean isValidCredentials = false;

        if (!Cryptographer.isBase64(password)) {
            password = Cryptographer.sha1binBase64(password);
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        if (!url.endsWith("/")) {
            url += "/";
        }

        Framework framework = Framework.getFramework(url, username, password, domainId,
                mSharedPref.hasFace(), mSharedPref.hasVoice(), mSharedPref.hasLiveness(),
                mSharedPref.isDebugMode(), mSharedPref.getCameraQuality());

        try {
            isValidCredentials = framework.isValidCredentials();
        } catch (CoreException e) {
            mActivity.hideProgress();

            String reason;

            if (e instanceof ServiceUnavailableException) {
                mActivity.showMessage(mActivity.getString(R.string.error_server_is_unavailable));
            } else if (e instanceof NotFoundException) {
                mActivity.setErrorURL();
                mActivity.saveButtonDisabled();
            } else {
                reason = ((RestException) e).reason;
                if (reason.contentEquals("INCORRECT_PASSWORD")) {
                    mActivity.setErrorPassword();
                    mActivity.saveButtonDisabled();
                } else if (reason.contentEquals("USER_BLOCKED")) {
                    mActivity.setErrorUsername(true);
                    mActivity.saveButtonDisabled();
                } else if (reason.contentEquals("USER_NOT_FOUND")) {
                    mActivity.setErrorUsername(false);
                    mActivity.saveButtonDisabled();
                } else if (reason.contentEquals("UNKNOWN_DOMAIN")) {
                    mActivity.setErrorDomainId();
                    mActivity.saveButtonDisabled();
                }
            }
        }

        if (isValidCredentials) {
            mActivity.hideProgress();
            mActivity.saveButtonDisabled();
            mSharedPref.setServerCredentials(new ServerSettingsCredentials(url, username, password, String.valueOf(domainId)));
            mActivity.finish();
        }
    }

}
