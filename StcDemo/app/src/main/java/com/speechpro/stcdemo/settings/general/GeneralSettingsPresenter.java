package com.speechpro.stcdemo.settings.general;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.speechpro.onepass.framework.Framework;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;
import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;
import com.speechpro.stcdemo.common.AppInfo;
import com.speechpro.stcdemo.common.SharedPref;
import com.speechpro.stcdemo.settings.server.ServerSettingsActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Created by Alexander Grigal on 06.02.18.
 */

public class GeneralSettingsPresenter {

    private static final String TAG = GeneralSettingsPresenter.class.getSimpleName();

    private GeneralSettingsActivity mActivity;

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler();

    private CameraQuality mCameraQuality;

    @Inject
    SharedPref mSharedPref;

    @Inject
    AppInfo mAppInfo;

    GeneralSettingsActivity getActivity() {
        return mActivity;
    }

    public GeneralSettingsPresenter() {
        STCDemoApp.getInstance().getAppComponent().inject(this);
        mCameraQuality = mSharedPref.getCameraQuality();
    }

    public void setActivity(GeneralSettingsActivity activity) {
        this.mActivity = activity;
    }

    boolean hasFace() {
        return mSharedPref.hasFace();
    }

    boolean hasVoice() {
        return mSharedPref.hasVoice();
    }

    boolean hasLiveness() {
        return mSharedPref.hasLiveness();
    }

    boolean isDebugMode() {
        return mSharedPref.isDebugMode();
    }

    void onClickSettingsServer() {
        mActivity.startActivity(new Intent(mActivity, ServerSettingsActivity.class));
    }

    void onClickDeleteUser() {
        mActivity.showDialogDeleteUser(mSharedPref.getLogin());
    }

    void onClickFace(boolean hasFace) {
        if (checkModality()) {
            mActivity.setFace(hasFace);
        } else {
            mActivity.setFace(!hasFace);
        }
    }

    void onClickVoice(boolean hasVoice) {
        if (checkModality()) {
            mActivity.setVoice(hasVoice);
        } else {
            mActivity.setVoice(!hasVoice);
        }
    }

    void onClickLiveness(boolean hasLiveness) {
        if (checkModality()) {
            mActivity.setLiveness(hasLiveness);
        } else {
            mActivity.setLiveness(!hasLiveness);
        }
    }

    void onClickResolution() {
        showSupportedResolutionDialog();
    }

    void onClickDebugMode(boolean isDebugMode) {
        mActivity.setDebugMode(isDebugMode);
        mSharedPref.setDebugMode(isDebugMode);
    }

    void onClickVersion() {
        mActivity.showToastMessage(String.format(mActivity.getString(R.string.online_version),
                mAppInfo.getVersionName(),
                mAppInfo.getVersionCode()));
    }

    void onClickLowResolution() {
        mCameraQuality = CameraQuality.LOW;
    }

    void onClickMediumResolution() {
        mCameraQuality = CameraQuality.MEDIUM;
    }

    void saveModalityState(boolean hasFace, boolean hasVoice, boolean hasLiveness) {
        mSharedPref.setFace(hasFace);
        mSharedPref.setVoice(hasVoice);
        mSharedPref.setLiveness(hasLiveness);
    }

    void deleteUser(final String login) {
        mActivity.showProgress();

        mExecutorService.submit(new Runnable() {
            Framework framework = Framework.getFramework(
                    mSharedPref.getServerCredentials().url,
                    mSharedPref.getServerCredentials().username,
                    mSharedPref.getServerCredentials().password,
                    Integer.parseInt(mSharedPref.getServerCredentials().domainId),
                    mSharedPref.hasFace(),
                    mSharedPref.hasVoice(),
                    mSharedPref.hasLiveness(),
                    mSharedPref.isDebugMode(),
                    mSharedPref.getCameraQuality());

            @Override
            public void run() {
                try {
                    final boolean hasDeleted = framework.delete(login);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.hideProgress();
                            mActivity.showSnackbarMessage(hasDeleted ? R.string.text_user_removed : R.string.text_no_account);
                        }
                    });
                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.hideProgress();
                            mActivity.showSnackbarMessage(R.string.network_error);
                        }
                    });
                    Log.e(TAG, "Connection failed", e);
                }
            }
        });
    }

    private void showSupportedResolutionDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.resolutions_dialog, null);
        RadioButton low = (RadioButton) dialogView.findViewById(R.id.low_resolution);
        RadioButton medium = (RadioButton) dialogView.findViewById(R.id.medium_resolution);
        if (mSharedPref.getCameraQuality() == CameraQuality.LOW) {
            low.setChecked(true);
        } else {
            medium.setChecked(true);
        }
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(mActivity.getString(R.string.camera_resolution));
        dialogBuilder.setMessage(mActivity.getString(R.string.select_resolution));
        dialogBuilder.setPositiveButton(mActivity.getString(R.string.choose), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mSharedPref.setCameraQuality(mCameraQuality);
            }
        });
        dialogBuilder.setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog builder = dialogBuilder.create();
        builder.show();

    }

    private boolean checkModality() {
        boolean hasFace = mActivity.mFaceCheckbox.isChecked();
        boolean hasVoice = mActivity.mVoiceCheckbox.isChecked();
        boolean hasLiveness = mActivity.mLivenessCheckbox.isChecked();

        if (!hasFace && !hasVoice && !hasLiveness) {            //0
            mActivity.livenessDisabled();
            mActivity.showSnackbarMessage(mActivity.getString(R.string.modality_must_be_enabled));
            return false;
        } else if (!hasFace && !hasVoice && hasLiveness) {      //1
            mActivity.livenessDisabled();
            mActivity.showSnackbarMessage(mActivity.getString(R.string.modality_must_be_enabled));
            return false;
        } else if (!hasFace && hasVoice && !hasLiveness) {      //2
            mActivity.saveLivenessState();
            mActivity.livenessDisabled();
            return true;
        } else if (!hasFace && hasVoice && hasLiveness) {       //3
            mActivity.saveLivenessState();
            mActivity.livenessDisabled();
            mActivity.setLiveness(false);
            return true;
        } else if (hasFace && !hasVoice && !hasLiveness) {      //4
            mActivity.saveLivenessState();
            mActivity.livenessDisabled();
            mActivity.setLiveness(false);
            return true;
        } else if (hasFace && !hasVoice && hasLiveness) {       //5
            mActivity.saveLivenessState();
            mActivity.livenessDisabled();
            mActivity.setLiveness(false);
            return true;
        } else if (hasFace && hasVoice && !hasLiveness) {       //6
            mActivity.livenessEnabled();
            return true;
        } else if (hasFace && hasVoice && hasLiveness) {        //7
            mActivity.livenessEnabled();
            return true;
        }

        return false;
    }

}
