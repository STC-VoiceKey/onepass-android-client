package com.speechpro.stcdemo.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.app.STCDemoApp;
import com.speechpro.stcdemo.login.LoginActivity;

import javax.inject.Inject;

/**
 * Created by Alexander Grigal on 24.01.18.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int AUTO_HIDE_DELAY_MILLIS = 500;

    @Inject
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        STCDemoApp.getInstance().getAppComponent().createSplashscreenComponent()
                .injectSplashscreenActivity(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSplashPresenter.delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    public void onNext() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Inject
    void setActivity() {
        mSplashPresenter.setActivity(this);
    }
}
