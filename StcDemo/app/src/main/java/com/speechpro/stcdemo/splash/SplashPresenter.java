package com.speechpro.stcdemo.splash;

import android.os.Handler;

/**
 * Created by Alexander Grigal on 23.01.18.
 */
public class SplashPresenter {

    private SplashActivity mActivity;

    public void setActivity(SplashActivity activity) {
        this.mActivity = activity;
    }

    void delayedHide(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.onNext();
            }
        }, delayMillis);

    }

}
