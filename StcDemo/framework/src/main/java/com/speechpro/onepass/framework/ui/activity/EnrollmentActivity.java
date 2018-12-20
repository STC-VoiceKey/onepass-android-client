package com.speechpro.onepass.framework.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.ui.fragment.AgreementFragment;
import com.speechpro.onepass.framework.ui.fragment.PhotoFragment;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollDynamicEnrollVoiceFragment;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollResultFragment;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollStaticEnrollVoiceFragment;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;

/**
 * @author volobuev
 * @since 26.02.16
 */
public class EnrollmentActivity extends BaseActivity {

    private final static String TAG = EnrollmentActivity.class.getSimpleName();

//    private final static Logger LOG = LoggerFactory.getLogger(EnrollmentActivity.class);

    public static Intent getCallingIntent(Context context, String userId, String url, String sessionURL,
                                          String username, String password, int domainId,
                                          boolean hasFace, boolean hasDynamicVoice, boolean hasStaticVoice,
                                          boolean hasLiveness, boolean isDebugMode, CameraQuality cameraQuality) {
        return getCallingIntent(context, EnrollmentActivity.class, userId, url, sessionURL, username, password,
                domainId, hasFace, hasDynamicVoice, hasStaticVoice, hasLiveness, isDebugMode, cameraQuality);
    }

    public void finishFaceAgainFragment() {
        replaceFragment(currentFragment.first, currentFragment.second);
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
        setContentView(R.layout.a_enrollment);

        initializeActivity();

        presenter = new EnrollmentPresenter(model, this, userId);

        try {
            presenter.init();
        } catch (InternetConnectionException | RestException e) {
            showErrorMessage(R.string.network_error);
            finish();
            return;
        }

        nextEpisode();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeActivity() {
        getComponent().inject(this);

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new AgreementFragment()));
        if (this.hasFace) {
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new PhotoFragment()));
        }
        if (this.hasDynamicVoice) {
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollDynamicEnrollVoiceFragment()));
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollDynamicEnrollVoiceFragment()));
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollDynamicEnrollVoiceFragment()));
        } else if (this.hasStaticVoice) {
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollStaticEnrollVoiceFragment()));
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollStaticEnrollVoiceFragment()));
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollStaticEnrollVoiceFragment()));
        }
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollResultFragment()));
    }
}
