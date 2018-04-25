package com.speechpro.stcdemo.splash;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 23.01.18.
 */
@Module
public class SplashModule {

    @ActivityScope
    @Provides
    SplashPresenter provideSplashscreenPresenter() {
        return new SplashPresenter();
    }

}
