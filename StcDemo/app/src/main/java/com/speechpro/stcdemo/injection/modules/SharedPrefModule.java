package com.speechpro.stcdemo.injection.modules;

import android.content.Context;

import com.speechpro.stcdemo.common.SharedPref;
import com.speechpro.stcdemo.injection.AppScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
@Module
public class SharedPrefModule {

    @Provides
    @AppScope
    SharedPref provideSharedPref(Context ctx) {
        return new SharedPref(ctx);
    }

}
