package com.speechpro.stcdemo.login;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
@ActivityScope
@Subcomponent(modules = LoginModule.class)
public interface LoginComponent {

    void injectLoginActivity(LoginActivity activity);

}
