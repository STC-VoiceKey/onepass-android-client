package com.speechpro.stcdemo.settings.general;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alexander Grigal on 30.01.18.
 */

public class GeneralSettingsActivity extends AppCompatActivity {

    private static final String TAG = GeneralSettingsActivity.class.getSimpleName();

    @Inject
    GeneralSettingsPresenter mGeneralSettingsPresenter;

    @Inject
    void setActivity() {
        mGeneralSettingsPresenter.setActivity(this);
    }

    @BindView(R.id.progressbar) View mProgress;
    @BindView(R.id.main) View mLinearLayout;
    @BindView(R.id.tv_version) TextView mVersion;
    @BindView(R.id.face_checkbox) CheckBox mFaceCheckbox;
    @BindView(R.id.dynamic_voice_checkbox) CheckBox mDynamicVoiceCheckbox;
    @BindView(R.id.static_voice_checkbox) CheckBox mStaticVoiceCheckbox;
    @BindView(R.id.liveness_checkbox) CheckBox mLivenessCheckbox;
    @BindView(R.id.debug_mode) CheckBox mDebugMode;

    private boolean mhasLiveness;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        createGeneralSettingsComponent();
        initCheckBoxState();
        initVersionTextView();
    }

    @Override
    protected void onPause() {
        mGeneralSettingsPresenter.saveModalityState(
                mFaceCheckbox.isChecked(),
                mDynamicVoiceCheckbox.isChecked(),
                mStaticVoiceCheckbox.isChecked(),
                mLivenessCheckbox.isChecked());

        super.onPause();
    }

    private void createGeneralSettingsComponent() {
        STCDemoApp.getInstance().getAppComponent().createGeneralSettingsComponent()
                .injectGeneralSettingsActivity(this);
    }

    private void initCheckBoxState() {
        mFaceCheckbox.setChecked(mGeneralSettingsPresenter.hasFace());
        mDynamicVoiceCheckbox.setChecked(mGeneralSettingsPresenter.hasDynamicVoice());
        mStaticVoiceCheckbox.setChecked(mGeneralSettingsPresenter.hasStaticVoice());
        mLivenessCheckbox.setChecked(mGeneralSettingsPresenter.hasLiveness());
        if ((!mFaceCheckbox.isChecked() && mDynamicVoiceCheckbox.isChecked())
                || (mFaceCheckbox.isChecked() && !mDynamicVoiceCheckbox.isChecked())) {
            mLivenessCheckbox.setEnabled(false);
        }
        mDebugMode.setChecked(mGeneralSettingsPresenter.isDebugMode());
    }

    private void initVersionTextView() {
        mVersion.setText(String.format(this.getString(R.string.online_version),
                mGeneralSettingsPresenter.mAppInfo.getVersionName(),
                mGeneralSettingsPresenter.mAppInfo.getVersionCode()));
    }

    @OnClick(R.id.server_settings)
    public void onClickSettingsServer(View view) {
        mGeneralSettingsPresenter.onClickSettingsServer();
    }

    @OnClick(R.id.delete_user)
    public void onClickDeleteUser(View view) {
        mGeneralSettingsPresenter.onClickDeleteUser();
    }

    @OnClick(R.id.face_checkbox)
    public void onClickFaceCheckbox(View view) {
        mGeneralSettingsPresenter.onClickFace(mFaceCheckbox.isChecked());
    }

    @OnClick(R.id.dynamic_voice_checkbox)
    public void onClickDynamicVoiceCheckbox(View view) {
        if (mStaticVoiceCheckbox.isChecked()) {
            mStaticVoiceCheckbox.setChecked(false);
        }
        mGeneralSettingsPresenter.onClickDynamicVoice(mDynamicVoiceCheckbox.isChecked());
    }

    @OnClick(R.id.static_voice_checkbox)
    public void onClickStaticVoiceCheckbox(View view) {
        if (mDynamicVoiceCheckbox.isChecked()) {
            mDynamicVoiceCheckbox.setChecked(false);
        }
        mGeneralSettingsPresenter.onClickStaticVoice(mStaticVoiceCheckbox.isChecked());
    }

    @OnClick(R.id.liveness_checkbox)
    public void onClickLivenessCheckbox(View view) {
        mGeneralSettingsPresenter.onClickLiveness(mLivenessCheckbox.isChecked());
    }

    @OnClick(R.id.resolution)
    public void onClickResolution(View view) {
        mGeneralSettingsPresenter.onClickResolution();
    }

    @OnClick(R.id.debug_mode)
    public void onClickDebugModeCheckbox(View view) {
        mGeneralSettingsPresenter.onClickDebugMode(mDebugMode.isChecked());
    }

    @OnClick(R.id.version)
    public void onClickVersion(View view) {
        mGeneralSettingsPresenter.onClickVersion();
    }

    void showDialogDeleteUser(final String login) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.remove_user);
        builder.setMessage(String.format(getString(R.string.are_you_sure_you_want_to_remove_user),
                login != null ? login : ""));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (login != null && !login.isEmpty()) {
                    mGeneralSettingsPresenter.deleteUser(login);
                } else {
                    showSnackbarMessage(R.string.text_no_account);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    void showToastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    void showSnackbarMessage(int resId) {
        Snackbar.make(mLinearLayout, resId, Snackbar.LENGTH_LONG).show();
    }

    void showSnackbarMessage(String message) {
        Snackbar.make(mLinearLayout, message, Snackbar.LENGTH_LONG).show();
    }

    void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.GONE);
    }

    void hideProgress() {
        mProgress.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    void setFace(boolean hasFace) {
        mFaceCheckbox.setChecked(hasFace);
    }

    void setDynamicVoice(boolean hasVoice) {
        mDynamicVoiceCheckbox.setChecked(hasVoice);
    }

    void setStaticVoice(boolean hasVoice) {
        mStaticVoiceCheckbox.setChecked(hasVoice);
    }

    void setLiveness(boolean hasLiveness) {
        mLivenessCheckbox.setChecked(hasLiveness);
    }

    void livenessEnabled() {
        mLivenessCheckbox.setChecked(mhasLiveness);
        mLivenessCheckbox.setEnabled(true);
    }

    void livenessDisabled() {
        mLivenessCheckbox.setChecked(false);
        mLivenessCheckbox.setEnabled(false);
    }

    void saveLivenessState() {
        mhasLiveness = mLivenessCheckbox.isChecked();
    }

    void setDebugMode(boolean isChecked) {
        mDebugMode.setChecked(isChecked);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.low_resolution:
                if (checked) {
                    mGeneralSettingsPresenter.onClickLowResolution();
                }
                break;
            case R.id.medium_resolution:
                if (checked) {
                    mGeneralSettingsPresenter.onClickMediumResolution();
                }
                break;
        }
    }
}
