package com.speechpro.stcdemo.injection.modules;

import android.content.Context;

import com.speechpro.stcdemo.common.AppInfo;
import com.speechpro.stcdemo.injection.AppScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Grigal on 06.02.18.
 */
@Module
public class AppInfoModule {

    @Provides
    @AppScope
    AppInfo provideAppInfo(Context ctx) {
        return new AppInfo(ctx);
    }

}
