package com.speechpro.stcdemo.settings.general;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 06.02.18.
 */
@Module
public class GeneralSettingsModule {

    @ActivityScope
    @Provides
    GeneralSettingsPresenter provideGeneralSettingsPresenter() {
        return new GeneralSettingsPresenter();
    }

}
