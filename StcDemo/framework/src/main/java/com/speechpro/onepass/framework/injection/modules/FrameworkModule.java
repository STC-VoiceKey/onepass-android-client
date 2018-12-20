package com.speechpro.onepass.framework.injection.modules;

import com.speechpro.onepass.core.rest.api.RetroRestAPI;
import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.framework.util.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author volobuev
 * @since 31.05.16
 */
@Module
public class FrameworkModule {

    @Provides
    @Singleton
    ITransport provideModel() {
        return new RetroRestAPI(Constants.URL);
    }
}
