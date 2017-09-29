package com.speechpro.onepass.framework.ui.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import com.speechpro.onepass.framework.ui.view.threads.SinusWaveThread;

import java.lang.ref.WeakReference;

import static com.speechpro.onepass.framework.ui.view.threads.SinusWaveThread.VALUE;

/**
 * @author volobuev
 * @since 11.11.16
 */
public class SinusWaveView extends AutoFitTextureView implements TextureView.SurfaceTextureListener {

    private final static String TAG = SinusWaveView.class.getName();

    private SinusWaveThread mThread;


    public SinusWaveView(Context context) {
        super(context);
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    public SinusWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    public SinusWaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SinusWave texture ready");
        mThread = new SinusWaveThread(surface, getContext(), height, width);
        mThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SinusWave texture updated");
        sendMessage((short) 0);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "SinusWave texture destroyed");
        while (true) {
            try {
                if (mThread.getLooper() != null) {
                    mThread.getLooper().quit();
                }
                mThread.join();
                break;
            } catch (InterruptedException e) {
                Log.d(TAG, "SinusWave layer else in work");
            }
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void setAspectRatio(int width, int height) {
        Log.d(TAG, "Update brackets positions");
        super.setAspectRatio(width, height);
        sendMessage((short) 0);
    }

    public void process(short value) {
        sendMessage(value);
    }

    private void sendMessage(short value) {
        if (mThread != null) {
            if (mThread.isAlive() && mThread.getLooper() != null) {
                Message msg  = new Message();
                Bundle  data = new Bundle();
                data.putShort(VALUE, value);
                msg.setData(data);
                Handler handler = mThread.getHandler();
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        }
    }


}
