package com.speechpro.onepass.framework.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.speechpro.onepass.framework.injection.HasComponent;

/**
 *
 * @author volobuev
 * @since 14.01.2016
 */
public class FragmentShower {

    private Activity activity;
    private Fragment fragment;

    public FragmentShower(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    public void hide() {
        final FragmentManager fragmentManager = activity.getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    public void show() {
        final FragmentManager fragmentManager = activity.getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) activity).getComponent());
    }

}
