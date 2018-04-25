package com.speechpro.onepass.framework.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.ui.fragment.AgreementFragment;
import com.speechpro.onepass.framework.ui.fragment.PhotoFragment;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollResultFragment;
import com.speechpro.onepass.framework.ui.fragment.enroll.EnrollVoiceFragment;

/**
 * @author volobuev
 * @since 26.02.16
 */
public class EnrollmentActivity extends BaseActivity {

    private final static String TAG = EnrollmentActivity.class.getSimpleName();

//    private final static Logger LOG = LoggerFactory.getLogger(EnrollmentActivity.class);

    public static Intent getCallingIntent(Context context, String userId, String url,
                                          String username, String password, int domainId,
                                          boolean hasFace, boolean hasVoice, boolean hasLiveness,
                                          boolean isDebugMode, CameraQuality cameraQuality) {
        return getCallingIntent(context, EnrollmentActivity.class, userId, url, username, password,
                domainId, hasFace, hasVoice, hasLiveness, isDebugMode, cameraQuality);
    }

    public void finishFaceAgainFragment() {
        replaceFragment(currentFragment.first, currentFragment.second);
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
        setContentView(R.layout.a_enrollment);

        initializeActivity();

        presenter = new EnrollmentPresenter(model, this, userId);

        try {
            presenter.init();
        } catch (CoreException e) {
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
        if (this.hasVoice) {
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollVoiceFragment()));
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollVoiceFragment()));
            addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollVoiceFragment()));
        }
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollResultFragment()));
    }
}
