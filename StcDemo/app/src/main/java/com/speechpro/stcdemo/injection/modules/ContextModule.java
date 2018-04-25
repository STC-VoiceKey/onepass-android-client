package com.speechpro.stcdemo.injection.modules;

import android.content.Context;

import com.speechpro.stcdemo.injection.AppScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
@Module
public class ContextModule {

    Context mContext;

    public ContextModule(Context ctx) {
        this.mContext = ctx;
    }

    @Provides
    @AppScope
    Context provideContext() {
        return mContext;
    }

}
