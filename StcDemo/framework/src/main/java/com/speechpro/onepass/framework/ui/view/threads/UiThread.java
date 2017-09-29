package com.speechpro.onepass.framework.ui.view.threads;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author volobuev
 * @since 08.11.16
 */
public class UiThread extends Thread {

    private final static String TAG = UiThread.class.getName();

    protected final SurfaceTexture mTexture;
    protected final Context        mContext;

    protected volatile Looper  mLooper;
    protected volatile Handler mHandler;


    UiThread(SurfaceTexture mTexture, Context mContext) {
        this.mTexture = mTexture;
        this.mContext = mContext;
    }

    @Override
    public void run() {
        Log.d(TAG, "UiThread is running...");
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
    }

    @Nullable
    public Looper getLooper() {
        synchronized (this) {
            while (mLooper == null) {
                Log.d(TAG, "Lopper is null");
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
        return mLooper;
    }

    public synchronized Handler getHandler() {
        return mHandler;
    }
}
