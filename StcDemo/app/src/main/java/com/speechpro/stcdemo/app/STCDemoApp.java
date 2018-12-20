package com.speechpro.stcdemo.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.speechpro.stcdemo.injection.AppComponent;
import com.speechpro.stcdemo.injection.DaggerAppComponent;
import com.speechpro.stcdemo.injection.modules.ContextModule;


/**
 * Created by Alexander Grigal on 23.01.18.
 */
public class STCDemoApp extends Application {

    private static STCDemoApp sInstance;

    private AppComponent appComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
