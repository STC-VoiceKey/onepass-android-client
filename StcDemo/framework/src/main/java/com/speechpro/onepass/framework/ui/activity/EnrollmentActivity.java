package com.speechpro.onepass.framework.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.EnrollmentPresenter;
import com.speechpro.onepass.framework.ui.fragment.AgreementFragment;
import com.speechpro.onepass.framework.ui.fragment.EnrollFaceFragment;
import com.speechpro.onepass.framework.ui.fragment.EnrollResultFragment;
import com.speechpro.onepass.framework.ui.fragment.EnrollVoiceFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author volobuev
 * @since 26.02.16
 */
public class EnrollmentActivity extends BaseActivity {

    private final static String TAG = EnrollmentActivity.class.getSimpleName();

    private final static Logger LOG = LoggerFactory.getLogger(EnrollmentActivity.class);

    public static Intent getCallingIntent(Context context, String userId, String url) {
        return getCallingIntent(context, EnrollmentActivity.class, userId, url);
    }

    public void finishFaceAgainFragment() {
        replaceFragment(currentFragment.first, currentFragment.second);
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

        presenter = new EnrollmentPresenter(model, this, userId);

        nextEpisode();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
//        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeActivity() {
        getComponent().inject(this);

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new AgreementFragment()));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollFaceFragment()));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollVoiceFragment()));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollVoiceFragment()));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollVoiceFragment()));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new EnrollResultFragment()));
    }
}
