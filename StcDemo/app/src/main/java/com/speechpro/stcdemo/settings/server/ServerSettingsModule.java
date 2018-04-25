package com.speechpro.stcdemo.settings.server;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 06.02.18.
 */
@Module
public class ServerSettingsModule {

    @ActivityScope
    @Provides
    ServerSettingsPresenter provideServerSettingsPresenter() {
        return new ServerSettingsPresenter();
    }

}
