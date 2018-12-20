package com.speechpro.onepass.framework.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.ui.fragment.PhotoFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyResultFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyStaticVoiceFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyVideoFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyDynamicVoiceFragment;
import com.speechpro.onepass.framework.ui.fragment.verify.VerifyVoiceWithPhotoFragment;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;

/**
 * @author volobuev
 * @since 26.02.16
 */
public class VerificationActivity extends BaseActivity {

    public static Intent getCallingIntent(Context context, String userId, String url, String sessionURL,
                                          String username, String password, int domainId,
                                          boolean hasFace, boolean hasDynamicVoice, boolean hasStaticVoice,
                                          boolean hasLiveness, boolean isDebugMode, CameraQuality cameraQuality) {
        return getCallingIntent(context, VerificationActivity.class, userId, url, sessionURL, username, password,
                domainId, hasFace, hasDynamicVoice, hasStaticVoice, hasLiveness, isDebugMode, cameraQuality);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(INSTANCE_STATE_PARAM_USER_ID, userId);
            outState.putString(INSTANCE_STATE_PARAM_SERVER_URL, serverUrl);
            outState.putString(INSTANCE_STATE_PARAM_USERNAME, username);
            outState.putString(INSTANCE_STATE_PARAM_PASSWORD, password);
            outState.putInt(INSTANCE_STATE_PARAM_DOMAIN_ID, domainId);
            outState.putBoolean(INSTANCE_STATE_PARAM_FACE, hasFace);
            outState.putBoolean(INSTANCE_STATE_PARAM_DYNAMIC_VOICE, hasDynamicVoice);
            outState.putBoolean(INSTANCE_STATE_PARAM_STATIC_VOICE, hasStaticVoice);
            outState.putBoolean(INSTANCE_STATE_PARAM_LIVENESS, hasLiveness);
            outState.putBoolean(INSTANCE_STATE_PARAM_DEBUG_MODE, isDebugMode);
            outState.putSerializable(INSTANCE_STATE_PARAM_CAMERA_QUALITY, cameraQuality);
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
        } catch (InternetConnectionException | RestException e) {
            showErrorMessage(R.string.network_error);
            finish();
            return;
        }

        nextEpisode();
    }

    private void initializeActivity() {
        getComponent().inject(this);

        if (hasLiveness) {
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyVideoFragment())); //only video
        } else {
            if (hasFace && hasDynamicVoice) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyVoiceWithPhotoFragment())); //photo with voice
            } else if (hasFace && hasStaticVoice) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new PhotoFragment()));
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyStaticVoiceFragment()));
            } else if (hasFace) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new PhotoFragment()));
            } else if (hasDynamicVoice) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyDynamicVoiceFragment()));
            } else if (hasStaticVoice) {
                addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyStaticVoiceFragment()));
            }
        }

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyResultFragment()));
    }

}
