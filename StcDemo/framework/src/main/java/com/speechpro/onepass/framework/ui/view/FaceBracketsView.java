package com.speechpro.onepass.framework.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.view.threads.BracketsSurfaceThread;

import java.lang.ref.WeakReference;

import static com.speechpro.onepass.framework.ui.view.threads.BracketsSurfaceThread.FACE_DETECTED;
import static com.speechpro.onepass.framework.ui.view.threads.BracketsSurfaceThread.FACE_LOST;

/**
 * @author volobuev
 * @since 12.10.16
 */
public class FaceBracketsView extends AutoFitTextureView implements TextureView.SurfaceTextureListener {

    private final static String TAG = FaceBracketsView.class.getName();

    private final static int DEFAULT_X = 70;
    private final static int DEFAULT_Y = 70;

    private int mFaceXPercent;
    private int mFaceYPercent;

    private WeakReference<BracketsSurfaceThread> mThread;


    public FaceBracketsView(Context context) {
        super(context);
        mFaceXPercent = DEFAULT_X;
        mFaceYPercent = DEFAULT_Y;
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    public FaceBracketsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    public FaceBracketsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttributes(attrs);
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    public void faceDetected(int count) {
        Log.d(TAG, "Face detected -> View");
        sendMessage(FACE_DETECTED, count);
    }

    public void faceLost(int count) {
        Log.d(TAG, "Face lost -> View");
        sendMessage(FACE_LOST, count);
    }

    @Override
    public void setAspectRatio(int width, int height) {
        Log.d(TAG, "Update brackets positions");
        super.setAspectRatio(width, height);
        changeSurface(width, height);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "Brackets texture ready");
        mThread = new WeakReference<>(new BracketsSurfaceThread(surface, getContext(), height, width));
        mThread.get().start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "Brackets texture updated");
        changeSurface(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "Brackets texture destroyed");
        while (true) {
            try {
                if (mThread.get().getLooper() != null) { mThread.get().getLooper().quit(); }
                mThread.get().join();
                break;
            } catch (InterruptedException e) {
                Log.d(TAG, "Brackets layer else in work");
            }
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void sendMessage(int type, int count) {
        if (mThread != null && mThread.get() != null && mThread.get().getLooper() != null) {
            Message msg = new Message();
            msg.what = type;
            Bundle data = new Bundle();
            data.putInt(BracketsSurfaceThread.COUNT, count);
            msg.setData(data);
            mThread.get().getHandler().sendMessage(msg);
        }
    }

    private void changeSurface(int width, int height) {
        if (mThread != null && mThread.get() != null && mThread.get().getLooper() != null) {
            Message msg = new Message();
            msg.what = BracketsSurfaceThread.START_STATE;
            Bundle data = new Bundle();
            data.putInt(BracketsSurfaceThread.COUNT, 0);
            msg.setData(data);
            mThread.get().getHandler().sendMessage(msg);
        }
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray attrsArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FaceBorderView, 0, 0);
        try {
            mFaceXPercent = attrsArray.getInt(R.styleable.FaceBorderView_xFace, DEFAULT_X);
        } catch (Exception e) {
            mFaceXPercent = DEFAULT_X;
        }
        try {
            mFaceYPercent = attrsArray.getInt(R.styleable.FaceBorderView_yFace, DEFAULT_Y);
        } catch (Exception e) {
            mFaceYPercent = DEFAULT_Y;
        } finally {
            attrsArray.recycle();
        }

        if ((mFaceXPercent > 95) || (mFaceXPercent < 10)) { mFaceXPercent = DEFAULT_X; }
        if ((mFaceYPercent > 95) || (mFaceYPercent < 10)) { mFaceYPercent = DEFAULT_Y; }
    }
}
