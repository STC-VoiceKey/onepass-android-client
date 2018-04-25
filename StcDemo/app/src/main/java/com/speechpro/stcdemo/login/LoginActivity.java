package com.speechpro.stcdemo.login;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;
import com.speechpro.stcdemo.permission.PermissionCallback;
import com.speechpro.stcdemo.permission.PermissionUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.speechpro.onepass.framework.util.Constants.ENROLL_REQUEST_CODE;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
public class LoginActivity extends AppCompatActivity
        implements TextWatcher, ActivityCompat.OnRequestPermissionsResultCallback, PermissionCallback {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private final long DELAY = 700;
    private final Handler handler = new Handler();
    private Runnable mRunnable;

    private Dialog mDialog;

    private ArrayList<String> permissions = new ArrayList<>();
    private PermissionUtils permissionUtils;

    private TypeTransition typeTransition;

    @Bind(R.id.layout_warning) View mWarning;
    @Bind(R.id.email) EditText mEmailEditText;
    @Bind(R.id.login) View mLogin;
    @Bind(R.id.signup) View mSignup;

    @Inject
    LoginPresenter mLoginPresenter;

    @Inject
    void setActivity() {
        mLoginPresenter.setActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        createLoginComponent();

        permissionUtils = new PermissionUtils(this);

        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        mLoginPresenter.registerBroadcastReceiver();
    }

    private void createLoginComponent() {
        STCDemoApp.getInstance().getAppComponent().createLoginComponent()
                .injectLoginActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEmailEditText.addTextChangedListener(this);
        mEmailEditText.setText(mLoginPresenter.getLogin());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEmailEditText.removeTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresenter.unregisterBroadcastReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENROLL_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                String res = data.getStringExtra(ACTIVITY_RESULT);
//                if (res.equals(SUCCES)) {
//                    mLoginPresenter.startVerification(mEmailEditText.getText().toString());
//                }
//            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        handler.removeCallbacks(mRunnable);
    }

    @Override
    public void afterTextChanged(final Editable email) {
        //button disable, because you can access UI here
        mSignup.setEnabled(false);
        mLogin.setEnabled(false);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "afterTextChanged: " + email);
                mLoginPresenter.checkEnroll(email.toString());
            }
        };
        handler.postDelayed(mRunnable, DELAY);
    }

    @OnClick(R.id.iv_settings)
    public void onClickSettings(View view) {
        mLoginPresenter.setLogin(mEmailEditText.getText().toString());
        mLoginPresenter.showSettings(mEmailEditText.getText().toString());
    }

    @OnClick(R.id.login)
    public void onClickLogin(View view) {
        mLoginPresenter.setLogin(mEmailEditText.getText().toString());
        typeTransition = TypeTransition.VERIFICATION;
        permissionUtils.checkPermission(permissions, getString(R.string.give_necessary_permissions), 1);
    }

    @OnClick(R.id.signup)
    public void onClickSignUp(View view) {
        mLoginPresenter.setLogin(mEmailEditText.getText().toString());
        typeTransition = TypeTransition.ENROLLMENT;
        permissionUtils.checkPermission(permissions, getString(R.string.give_necessary_permissions), 1);
    }

    void showProgress() {
        if (mDialog == null) {
            mDialog = new Dialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_layout);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    void hideProgress() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    void showIncorrectEmail() {
        mLogin.setEnabled(false);
        mSignup.setEnabled(false);
        mWarning.setVisibility(View.VISIBLE);
    }

    void hideIncorrectEmail() {
        mWarning.setVisibility(View.INVISIBLE);
    }

    void showError(String message) {
        View view = getCurrentFocus();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }
    }

    void signUpEnabled() {
        mLogin.setEnabled(false);
        mSignup.setEnabled(true);
    }

    void loginEnabled() {
        mLogin.setEnabled(true);
        mSignup.setEnabled(false);
    }

    void buttonsDisabled() {
        mLogin.setEnabled(false);
        mSignup.setEnabled(false);
    }

    void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void PermissionGranted(int requestCode) {
        Log.d(TAG, "PermissionGranted: ");

        switch (typeTransition) {
            case ENROLLMENT:
                mLoginPresenter.startEnrollment(mEmailEditText.getText().toString());
                break;
            case VERIFICATION:
                mLoginPresenter.startVerification(mEmailEditText.getText().toString());
                break;
        }
    }

    @Override
    public void PartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions) {
        Log.d(TAG, "PartialPermissionGranted: ");
    }

    @Override
    public void PermissionDenied(int requestCode) {
        Log.d(TAG, "PermissionDenied: ");
    }

    @Override
    public void NeverAskAgain(int requestCode) {
        Log.d(TAG, "NeverAskAgain: ");
    }

    private enum TypeTransition {ENROLLMENT, VERIFICATION}

}
