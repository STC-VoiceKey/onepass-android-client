package com.speechpro.stcdemo.settings.server;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;

import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alexander Grigal on 06.02.18.
 */

public class ServerSettingsActivity extends AppCompatActivity implements TextWatcher, View.OnTouchListener {

    private static final String TAG = ServerSettingsActivity.class.getSimpleName();

    // Pattern for recognizing a URL, based off RFC 3986
    private final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @BindView(R.id.progressbar) View mProgressbar;
    @BindView(R.id.main) View mLinearLayout;
    @BindView(R.id.url_server) EditText mURLServerEditText;
    @BindView(R.id.url_server_session) EditText mURLServerSessionEditText;
    @BindView(R.id.username) EditText mUsernameEditText;
    @BindView(R.id.password) EditText mPasswordEditText;
    @BindView(R.id.domain_id) EditText mDomainIdEditText;
    @BindView(R.id.save) Button mSaveButton;
    @BindView(R.id.default_settings) Button mDefaultSettingsButton;

    @Inject
    ServerSettingsPresenter mServerSettingsPresenter;

    @Inject
    void setActivity() {
        mServerSettingsPresenter.setActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        ButterKnife.bind(this);
        createServerSettingsComponent();

        mPasswordEditText.setOnTouchListener(this);

        mURLServerEditText.addTextChangedListener(this);
        mURLServerEditText.setText(mServerSettingsPresenter.getURLServer());
        mURLServerSessionEditText.addTextChangedListener(this);
        mURLServerSessionEditText.setText(mServerSettingsPresenter.getURLServerSession());
        mUsernameEditText.addTextChangedListener(this);
        mUsernameEditText.setText(mServerSettingsPresenter.getUsername());
        mPasswordEditText.addTextChangedListener(this);
        mPasswordEditText.setText(mServerSettingsPresenter.getPassword());
        mDomainIdEditText.addTextChangedListener(this);
        mDomainIdEditText.setText(mServerSettingsPresenter.getDomainId());
    }

    private void createServerSettingsComponent() {
        STCDemoApp.getInstance().getAppComponent().createServerSettingsComponent()
                .injectServerSettingsActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mURLServerEditText.removeTextChangedListener(this);
        mURLServerSessionEditText.removeTextChangedListener(this);
        mUsernameEditText.removeTextChangedListener(this);
        mPasswordEditText.removeTextChangedListener(this);
        mDomainIdEditText.removeTextChangedListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mPasswordEditText.getText().clear();
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        mServerSettingsPresenter.checkChangedCredentials(
                mURLServerEditText.getText().toString(),
                mURLServerSessionEditText.getText().toString(),
                mUsernameEditText.getText().toString(),
                mPasswordEditText.getText().toString(),
                mDomainIdEditText.getText().toString());

        mServerSettingsPresenter.checkDefaultCredentials(
                mURLServerEditText.getText().toString(),
                mURLServerSessionEditText.getText().toString(),
                mUsernameEditText.getText().toString(),
                mPasswordEditText.getText().toString(),
                mDomainIdEditText.getText().toString());
    }

    @OnClick(R.id.save)
    public void onClickSave(View view) {
        resetErrors();
        attemtSave();
    }

    @OnClick(R.id.default_settings)
    public void onClickDefaultSettings(View view) {
        resetErrors();

        mURLServerEditText.setText(R.string.url_server);
        mURLServerSessionEditText.setText(R.string.url_server_session);
        mUsernameEditText.setText(R.string.username);
        mPasswordEditText.setText(R.string.password);
        mDomainIdEditText.setText(R.string.domain_id);
    }

    void saveButtonEnabled() {
        mSaveButton.setEnabled(true);
    }

    void saveButtonDisabled() {
        mSaveButton.setEnabled(false);
    }

    void defaultButtonEnabled() {
        mDefaultSettingsButton.setEnabled(true);
    }

    void defaultButtonDisabled() {
        mDefaultSettingsButton.setEnabled(false);
    }

    void showProgress() {
        mProgressbar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.GONE);
    }

    void hideProgress() {
        mProgressbar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    void setErrorURL() {
        mURLServerEditText.setError(getString(R.string.error_incorrect_url));
        mURLServerEditText.requestFocus();
    }

    void setErrorUsername(boolean isBlocked) {
        mUsernameEditText.setError(isBlocked
                ? getString(R.string.error_user_is_blocked)
                : getString(R.string.error_user_not_registered));
        mUsernameEditText.requestFocus();
    }

    void setErrorPassword() {
        mPasswordEditText.setError(getString(R.string.error_incorrect_password));
        mPasswordEditText.requestFocus();
    }

    void setErrorDomainId() {
        mDomainIdEditText.setError(getString(R.string.error_unknown_domain));
        mDomainIdEditText.requestFocus();
    }

    private void attemtSave() {
        String serverUrl = mURLServerEditText.getText().toString();
        String sessionUrl = mURLServerSessionEditText.getText().toString();
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String domainId = mDomainIdEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(serverUrl)) {
            mURLServerEditText.setError(getString(R.string.error_field_required));
            focusView = mURLServerEditText;
            cancel = true;
//        } else if (!Patterns.WEB_URL.matcher(sessionUrl).matches()) {
        } else if (!isValidURL(serverUrl)) {
            mURLServerEditText.setError(getString(R.string.error_not_valid_url));
            focusView = mURLServerEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(sessionUrl)) {
            mURLServerEditText.setError(getString(R.string.error_field_required));
            focusView = mURLServerEditText;
            cancel = true;
        } else if (!isValidURL(sessionUrl)) {
            mURLServerEditText.setError(getString(R.string.error_not_valid_url));
            focusView = mURLServerEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError(getString(R.string.error_field_required));
            focusView = mUsernameEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(domainId)) {
            mDomainIdEditText.setError(getString(R.string.error_field_required));
            focusView = mDomainIdEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mServerSettingsPresenter.checkValidCredentials(serverUrl, sessionUrl, username, password, Integer.parseInt(domainId));
        }
    }

    private void resetErrors() {
        mURLServerEditText.setError(null);
        mURLServerSessionEditText.setError(null);
        mUsernameEditText.setError(null);
        mPasswordEditText.setError(null);
        mDomainIdEditText.setError(null);
    }

    boolean isValidURL(String url) {
        return urlPattern.matcher(url).matches();
    }
}
