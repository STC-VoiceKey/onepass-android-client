package com.speechpro.onepass.framework.injection.modules;

import android.content.Context;

import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.Model;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author volobuev
 * @since 29.03.16
 */
@Module
public class UIModule {

    private final Context context;

    public UIModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContect() {
        return this.context;
    }


    @Provides
    @Singleton
    IModel provideModel() {
        return new Model();
    }

}
