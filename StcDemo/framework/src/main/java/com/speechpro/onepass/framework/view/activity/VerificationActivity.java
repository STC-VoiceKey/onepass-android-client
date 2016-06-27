package com.speechpro.onepass.framework.view.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.camera.PreviewCallback;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.view.fragment.*;

import javax.inject.Inject;


/**
 * @author volobuev
 * @since 26.02.16
 */
public class VerificationActivity extends BaseActivity {

    @Inject
    PreviewCallback previewCallback;

    private VideoBorderFragment borderFragment;
    private VideoFragment       videoFragment;
    private ResultFragment      resultFragment;

    public static Intent getCallingIntent(Context context, String userId, String url) {
        return getCallingIntent(context, VerificationActivity.class, userId, url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(INSTANCE_STATE_PARAM_USER_ID, this.userId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_verification);
        initializeActivity();
        presenter = new VerificationPresenter(model, previewCallback, this, userId);

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

        borderFragment = new VideoBorderFragment();
        videoFragment = new VideoFragment();
        resultFragment = new ResultFragment();

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, videoFragment));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, resultFragment));
    }
}
