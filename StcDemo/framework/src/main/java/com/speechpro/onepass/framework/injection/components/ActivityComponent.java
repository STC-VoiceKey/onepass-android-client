package com.speechpro.onepass.framework.injection.components;

import android.app.Activity;

import com.speechpro.onepass.framework.injection.PerActivity;
import com.speechpro.onepass.framework.injection.modules.ActivityModule;

import dagger.Component;

/**
 * @author volobuev
 * @since 11.04.16
 */
@PerActivity
@Component(dependencies = UIComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity activity();

}
