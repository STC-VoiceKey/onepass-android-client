package com.speechpro.stcdemo.settings.server;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.NotFoundException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.android.session.session_library.exception.ServiceUnavailableException;
import com.speechpro.onepass.framework.FrameworkFactory;
import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;
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

    String getURLServer() {
        return mSharedPref.getServerCredentials().serverUrl;
    }

    String getURLServerSession() {
        return mSharedPref.getServerCredentials().sessionUrl;
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

    void checkChangedCredentials(String serverUrl, String sessionUrl, String username, String password, String domainId) {
        if (!mSharedPref.getServerCredentials().equals(new ServerSettingsCredentials(serverUrl, sessionUrl, username, password, domainId))) {
            mActivity.saveButtonEnabled();
        } else {
            mActivity.saveButtonDisabled();
        }
    }

    void checkDefaultCredentials(String serverUrl, String sessionUrl, String username, String password, String domainId) {
        ServerSettingsCredentials defaultCredentials = new ServerSettingsCredentials(
                mActivity.getString(R.string.url_server),
                mActivity.getString(R.string.url_server_session),
                mActivity.getString(R.string.username), mActivity.getString(R.string.password),
                mActivity.getString(R.string.domain_id));
        if (defaultCredentials.equals(new ServerSettingsCredentials(serverUrl, sessionUrl, username, password, domainId))) {
            mActivity.defaultButtonDisabled();
        } else {
            mActivity.defaultButtonEnabled();
        }
    }

    private String checkURL(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    void checkValidCredentials(String serverUrl, String sessionUrl,
                               String username, String password, int domainId) {

        mActivity.showProgress();

        boolean isValidCredentials = false;

        FrameworkFactory.Framework framework =
                FrameworkFactory.get(checkURL(serverUrl), checkURL(sessionUrl),
                        username, password, domainId, mSharedPref.hasDynamicVoice(),
                        mSharedPref.hasStaticVoice(), mSharedPref.hasFace(),
                        mSharedPref.hasLiveness(), mSharedPref.isDebugMode(),
                        mSharedPref.getCameraQuality());
        try {
            isValidCredentials = framework.isValidCredentials();
        } catch (RestException e) {
            mActivity.hideProgress();
            String reason;
            if (e instanceof ServiceUnavailableException) {
                mActivity.showMessage(mActivity.getString(R.string.error_server_is_unavailable));
            } else if (e instanceof NotFoundException) {
                mActivity.setErrorURL();
                mActivity.saveButtonDisabled();
            } else {
                reason = e.getReason();
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
        } catch (InternetConnectionException e) {
            mActivity.hideProgress();
            mActivity.showMessage(mActivity.getString(R.string.error_server_is_unavailable));
        }

        if (isValidCredentials) {
            mActivity.hideProgress();
            mActivity.saveButtonDisabled();
            mSharedPref.setServerCredentials(new ServerSettingsCredentials(serverUrl, sessionUrl, username, password, String.valueOf(domainId)));
            mActivity.finish();
        }
    }

}
