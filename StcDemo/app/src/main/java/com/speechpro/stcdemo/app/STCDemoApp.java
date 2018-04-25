package com.speechpro.stcdemo.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.speechpro.stcdemo.injection.AppComponent;
import com.speechpro.stcdemo.injection.DaggerAppComponent;
import com.speechpro.stcdemo.injection.modules.ContextModule;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Alexander Grigal on 23.01.18.
 */

public class STCDemoApp extends Application {

    private static STCDemoApp sInstance;

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize crashlytics
        Fabric.with(this, new Crashlytics());
        sInstance = this;
        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public static STCDemoApp getInstance() {
        return sInstance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
