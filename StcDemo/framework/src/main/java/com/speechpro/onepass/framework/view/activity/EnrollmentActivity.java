package com.speechpro.onepass.framework.view.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.view.fragment.*;

import javax.inject.Inject;


/**
 * @author volobuev
 * @since 26.02.16
 */
public class EnrollmentActivity extends BaseActivity {

    @Inject
    PreviewCallback previewCallback;

    private FaceBorderFragment borderFragment;
    private FaceFragment       visionFragment;
    private VoiceFragment      voiceEpisod1;
    private VoiceFragment      voiceEpisod2;
    private VoiceFragment      voiceEpisod3;
    private RegResultFragment     resultFragment;

    public static Intent getCallingIntent(Context context, String userId, String url) {
        return getCallingIntent(context, EnrollmentActivity.class, userId, url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(INSTANCE_STATE_PARAM_USER_ID, this.userId);
            outState.putString(INSTANCE_STATE_PARAM_URL, this.url);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_enrollment);

        initializeActivity();

        presenter = new EnrollmentPresenter(model, previewCallback, this, userId);

        nextEpisode();
        addFragment(R.id.fragment_layout, borderFragment);
        isBorderAdded = true;
    }

    @Override
    protected void removerBorder() {
        if (isBorderAdded) {
            removeFragment(borderFragment);
            isBorderAdded = false;
        }
    }

    private void initializeActivity() {
        getComponent().inject(this);

        //TODO: rewrite
        borderFragment = new FaceBorderFragment();
        visionFragment = new FaceFragment();
        voiceEpisod1 = new VoiceFragment();
        voiceEpisod2 = new VoiceFragment();
        voiceEpisod3 = new VoiceFragment();
        resultFragment = new RegResultFragment();

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, visionFragment));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, voiceEpisod1));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, voiceEpisod2));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, voiceEpisod3));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, resultFragment));
    }
}
