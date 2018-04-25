package com.speechpro.stcdemo.injection;

import android.content.Context;

import com.speechpro.stcdemo.common.AppInfo;
import com.speechpro.stcdemo.common.SharedPref;
import com.speechpro.stcdemo.injection.modules.AppInfoModule;
import com.speechpro.stcdemo.injection.modules.ContextModule;
import com.speechpro.stcdemo.injection.modules.SharedPrefModule;
import com.speechpro.stcdemo.login.LoginComponent;
import com.speechpro.stcdemo.login.LoginPresenter;
import com.speechpro.stcdemo.settings.general.GeneralSettingsComponent;
import com.speechpro.stcdemo.settings.general.GeneralSettingsPresenter;
import com.speechpro.stcdemo.settings.server.ServerSettingsComponent;
import com.speechpro.stcdemo.settings.server.ServerSettingsPresenter;
import com.speechpro.stcdemo.splash.SplashComponent;

import dagger.Component;

/**
 * Created by Alexander Grigal on 23.01.18.
 */
@AppScope
@Component(modules = {ContextModule.class, AppInfoModule.class, SharedPrefModule.class})
public interface AppComponent {
    Context getContext();

    SharedPref getPrefUtils();

    AppInfo getAppInfoUtils();

    void inject(LoginPresenter presenter);

    void inject(GeneralSettingsPresenter presenter);

    void inject(ServerSettingsPresenter presenter);

    SplashComponent createSplashscreenComponent();

    LoginComponent createLoginComponent();

    GeneralSettingsComponent createGeneralSettingsComponent();

    ServerSettingsComponent createServerSettingsComponent();

}
