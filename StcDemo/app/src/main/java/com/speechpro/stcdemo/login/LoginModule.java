package com.speechpro.stcdemo.login;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
@Module
public class LoginModule {

    @ActivityScope
    @Provides
    LoginPresenter provideLoginPresenter() {
        return new LoginPresenter();
    }

}
