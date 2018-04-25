package com.speechpro.stcdemo.settings.general;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by Alexander Grigal on 06.02.18.
 */
@ActivityScope
@Subcomponent(modules = GeneralSettingsModule.class)
public interface GeneralSettingsComponent {

    void injectGeneralSettingsActivity(GeneralSettingsActivity activity);

}
