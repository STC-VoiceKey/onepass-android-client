package com.speechpro.stcdemo.splash;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by Alexander Grigal on 23.01.18.
 */
@ActivityScope
@Subcomponent(modules = SplashModule.class)
public interface SplashComponent {

    void injectSplashscreenActivity(SplashActivity activity);

}
