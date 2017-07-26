package com.speechpro.onepass.framework.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.VerificationPresenter;
import com.speechpro.onepass.framework.ui.fragment.*;

/**
 * @author volobuev
 * @since 26.02.16
 */
public class VerificationActivity extends BaseActivity {

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
        presenter = new VerificationPresenter(model, this, userId);

        nextEpisode();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        finish();
    }

    private void initializeActivity() {
        getComponent().inject(this);

        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyFragment()));
        addFragmentToQueue(new Pair<Integer, Fragment>(R.id.fragment_layout, new VerifyResultFragment()));
    }

}
