package com.speechpro.stcdemo.settings.server;

import com.speechpro.stcdemo.injection.ActivityScope;

import dagger.Subcomponent;

/**
 * Created by Alexander Grigal on 06.02.18.
 */
@ActivityScope
@Subcomponent(modules = ServerSettingsModule.class)
public interface ServerSettingsComponent {

    void injectServerSettingsActivity(ServerSettingsActivity activity);

}
