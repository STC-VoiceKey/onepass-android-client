package com.speechpro.stcdemo.settings.server;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;

import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.Bind;
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

    @Bind(R.id.progressbar) View mProgressbar;
    @Bind(R.id.main) View mLinearLayout;
    @Bind(R.id.url) EditText mURLEditText;
    @Bind(R.id.username) EditText mUsernameEditText;
    @Bind(R.id.password) EditText mPasswordEditText;
    @Bind(R.id.domain_id) EditText mDomainIdEditText;
    @Bind(R.id.save) Button mSaveButton;
    @Bind(R.id.default_settings) Button mDefaultSettingsButton;

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

        mURLEditText.addTextChangedListener(this);
        mURLEditText.setText(mServerSettingsPresenter.getUrl());
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

        mURLEditText.removeTextChangedListener(this);
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
        mServerSettingsPresenter.checkChangedCredentials(mURLEditText.getText().toString(),
                mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString(),
                mDomainIdEditText.getText().toString());

        mServerSettingsPresenter.checkDefaultCredentials(mURLEditText.getText().toString(),
                mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString(),
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

        mURLEditText.setText(R.string.url_vkopdm);
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
        mURLEditText.setError(getString(R.string.error_incorrect_url));
        mURLEditText.requestFocus();
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
        String url = mURLEditText.getText().toString();
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String domainId = mDomainIdEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(url)) {
            mURLEditText.setError(getString(R.string.error_field_required));
            focusView = mURLEditText;
            cancel = true;
//        } else if (!Patterns.WEB_URL.matcher(url).matches()) {
        } else if (!isValidURL(url)) {
            mURLEditText.setError(getString(R.string.error_not_valid_url));
            focusView = mURLEditText;
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
            mServerSettingsPresenter.checkValidCredentials(url, username, password, Integer.parseInt(domainId));
        }
    }

    private void resetErrors() {
        mURLEditText.setError(null);
        mUsernameEditText.setError(null);
        mPasswordEditText.setError(null);
        mDomainIdEditText.setError(null);
    }

    boolean isValidURL(String url) {
        if (urlPattern.matcher(url).matches()) {
            return true;
        }
        return false;
    }
}
