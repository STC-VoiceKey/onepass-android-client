package com.speechpro.onepass.framework.view.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;
import com.speechpro.onepass.framework.injection.HasComponent;
import com.speechpro.onepass.framework.injection.components.DaggerUIComponent;
import com.speechpro.onepass.framework.injection.components.UIComponent;
import com.speechpro.onepass.framework.injection.modules.ActivityModule;
import com.speechpro.onepass.framework.injection.modules.UIModule;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.Model;
import com.speechpro.onepass.framework.presenter.BasePresenter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Base {@link android.app.Activity} class for every Activity in this application.
 *
 * @author volobuev
 * @since 13.01.2016
 */
public abstract class BaseActivity extends Activity implements HasComponent<UIComponent> {

    protected IModel model;

    protected static final String INTENT_PARAM_USER_ID         = "com.speechpro.onepass.PARAM_USER_ID";
    protected static final String INTENT_PARAM_URL             = "com.speechpro.onepass.PARAM_URL";
    protected static final String INSTANCE_STATE_PARAM_USER_ID = "com.speechpro.onepass.INSTANCE_STATE_PARAM_USER_ID";
    protected static final String INSTANCE_STATE_PARAM_URL     = "com.speechpro.onepass.INSTANCE_STATE_PARAM_URL";

    protected String                  userId;
    protected String                  url;
    protected Pair<Integer, Fragment> currentFragment;
    protected BasePresenter           presenter;
    protected UIComponent             uiComponent;

    protected boolean                        isBorderAdded  = false;
    private   Queue<Pair<Integer, Fragment>> fragmentsQueue = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.userId = getIntent().getStringExtra(INTENT_PARAM_USER_ID);
            this.url = getIntent().getStringExtra(INTENT_PARAM_URL);
        } else {
            this.userId = savedInstanceState.getString(INSTANCE_STATE_PARAM_USER_ID);
            this.url = savedInstanceState.getString(INSTANCE_STATE_PARAM_URL);
        }

        model = new Model(url);
        initializeInjector();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
       this.finish();
    }

    public void nextEpisode() {
        if (fragmentsQueue.isEmpty()) {
            this.finish();
            return;
        }
        removerBorder();
        if (currentFragment != null) {
            removeFragment(currentFragment.second);
        }
        onFragment();
    }

    public static Intent getCallingIntent(Context context, Class clazz, String userId, String url) {
        Intent callingIntent = new Intent(context, clazz);
        callingIntent.putExtra(INTENT_PARAM_USER_ID, userId);
        callingIntent.putExtra(INTENT_PARAM_URL, url);

        return callingIntent;
    }

    /**
     * Get the framework component for dependency injection.
     *
     * @return {@link UIComponent}
     */
    public UIComponent getComponent() {
        return uiComponent;
    }

    public BasePresenter getPresenter() {
        return presenter;
    }

    public void refreshFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    protected abstract void removerBorder();

    protected void addFragmentToQueue(Pair<Integer, Fragment> pair) {
        fragmentsQueue.add(pair);
    }

    protected void onFragment() {
        currentFragment = fragmentsQueue.poll();
        addFragment(currentFragment.first, currentFragment.second);
    }

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Remove a {@link Fragment} from this activity's layout.
     *
     * @param fragment The fragment to be added.
     */
    protected void removeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link com.speechpro.onepass.framework.injection.modules.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    private void initializeInjector() {
        this.uiComponent = DaggerUIComponent.builder().uIModule(new UIModule(getApplicationContext())).build();
    }
}
