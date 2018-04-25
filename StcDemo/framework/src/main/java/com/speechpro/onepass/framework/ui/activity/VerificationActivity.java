package com.speechpro.onepass.framework.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.ui.fragment.PhotoFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyVoiceWithPhotoFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyResultFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyVoiceFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyVideoFragment;

/**
 * @author volobuev
 * @since 26.02.16
 */
public class VerificationActivity extends BaseActivity {

    public static Intent getCallingIntent(Context context, String userId, String url,
                                          String username, String password, int domainId,
                                          boolean hasFace, boolean hasVoice, boolean hasLiveness,
                                          boolean isDebugMode, CameraQuality cameraQuality) {
        return getCallingIntent(context, VerificationActivity.class, userId, url, username, password,
                domainId, hasFace, hasVoice, hasLiveness, isDebugMode, cameraQuality);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(INSTANCE_STATE_PARAM_USER_ID, this.userId);
            outState.putString(INSTANCE_STATE_PARAM_URL, this.url);
            outState.putString(INSTANCE_STATE_PARAM_USERNAME, this.username);
            outState.putString(INSTANCE_STATE_PARAM_PASSWORD, this.password);
            outState.putInt(INSTANCE_STATE_PARAM_DOMAIN_ID, this.domainId);
            outState.putBoolean(INSTANCE_STATE_PARAM_FACE, this.hasFace);
            outState.putBoolean(INSTANCE_STATE_PARAM_VOICE, this.hasVoice);
            outState.putBoolean(INSTANCE_STATE_PARAM_LIVENESS, this.hasLiveness);
            outState.putBoolean(INSTANCE_STATE_PARAM_DEBUG_MODE, this.isDebugMode);
            outState.putSerializable(INSTANCE_STATE_PARAM_CAMERA_QUALITY, this.cameraQuality);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_verification);
        initializeActivity();
        presenter = new VerificationPresenter(model, this, userId);

        try {
            presenter.init();
        } catch (CoreException e) {
            showErrorMessage(R.string.network_error);
            finish();
            return;
        }

        nextEpisode();
    }

    private void initializeActivity() {
        getComponent().inject(this);

        if (this.hasLiveness) {
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyVideoFragment())); //only video
        } else {
            if (this.hasFace && this.hasVoice) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyVoiceWithPhotoFragment())); //photo with voice
            } else if (this.hasFace) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new PhotoFragment()));
            } else if (this.hasVoice) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyVoiceFragment()));
            }
        }

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyResultFragment()));
    }

}
