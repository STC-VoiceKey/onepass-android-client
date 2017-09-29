package com.speechpro.onepass.framework.ui.view.threads;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Surface;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.util.DimensionUtils;

import static java.lang.Math.PI;

/**
 * @author volobuev
 * @since 07.11.16
 */
public class SinusWaveThread extends UiThread {

    public final static  String VALUE = "VALUE";
    private final static String TAG   = SinusWaveThread.class.getName();

    private float mHeight;
    private float mWidth;
    private short mValue;


    private int   mWaveCount        = 5;
    private float mFrequency        = 1.5f;
    private float mPhase            = 0;
    private float mAmplitude        = 1f;
    private float mIdleAmplitude    = 1f;
    private float mDampingAmplitude = 1f;
    private float mDampingFactor    = 0.86f;
    private float mPhaseShift       = -0.15f;
    private float mDensity          = 5.0f;
    private float mMarginLeft       = 0f;
    private float mMarginRight      = 0f;
    private float mLineWidth        = 2f;

    private boolean mIsListen = true;


    private final int mWaveColor;
    private final int mBackgroundColor;
    private final int mCursorDelta;

    private final Paint mWavePaint;
    private final Paint mBackgroundPaint;


    public SinusWaveThread(SurfaceTexture mTexture, Context mContext, float mHeight, float mWidth) {
        super(mTexture, mContext);
        this.mHeight = mHeight;
        this.mWidth = mWidth;

        mWaveColor = ContextCompat.getColor(mContext, R.color.wave);
        mBackgroundColor = ContextCompat.getColor(mContext, android.R.color.transparent);

        mCursorDelta = DimensionUtils.convertDipToPixels(mContext, 2) / 2;

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setStrokeWidth(mCursorDelta * 2);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void run() {
        super.run();
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stateData = msg.getData();
                if (stateData != null) {
                    mValue = stateData.getShort(VALUE);
                }
                draw(mValue);
                return true;
            }
        });
        Looper.loop();
    }

    private void draw(short mValue) {

        Canvas  canvas  = null;
        Surface surface = null;
        try {

            synchronized (mTexture) {
                surface = new Surface(mTexture);
            }
            canvas = surface.lockCanvas(null);
            canvas.drawColor(mBackgroundColor, PorterDuff.Mode.CLEAR);
            canvas.drawColor(mBackgroundColor);

            float halfHeight = mHeight / 2;
            float width      = mWidth - mMarginLeft - mMarginRight;
            float mid        = width / 2.0f;
            float stepLength = mDensity / width;

            processValue(mValue);

            // We draw multiple sinus waves, with equal phases but altered amplitudes, multiplied by a parable function.
            for (int i = 0; i < mWaveCount + 1; i++) {
                float maxAmplitude = halfHeight - 4; // 4 corresponds to twice the stroke width

                // Progress is a value between 1.0 and -0.5, determined by the current wave idx, which is used to alter the wave's amplitude.
                float progress        = 1.0f - (float) i / mWaveCount;
                float normedAmplitude = (1.5f * progress - 0.5f) * mAmplitude;

                float finalLineWidth = (i == 0) ? mLineWidth : (mLineWidth / 2.0f);
                mWavePaint.setStrokeWidth(finalLineWidth);

                float lastX = mMarginLeft;
                float lastY = halfHeight;
                canvas.drawLine(0, halfHeight, lastX, lastY, mWavePaint);

                for (float x = 0; x < width + mDensity; x += mDensity) {

                    // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                    float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);

                    float y = (float) (scaling * normedAmplitude * mValue * Math.cos(2 * PI * (x / width)) + halfHeight);

                    float lx = x + mMarginLeft;
                    canvas.drawLine(lastX, lastY, lx, y, mWavePaint);

                    lastX = lx;
                    lastY = y;
                }
                canvas.drawLine(mWidth - mMarginRight, halfHeight, mWidth, halfHeight, mWavePaint);
            }
        } finally {
            if (canvas != null) { surface.unlockCanvasAndPost(canvas); }
            if (surface != null) { surface.release(); }
        }
    }

    private void processValue(short mValue) {
        // Get the recorder's current average power for the first channel, sanitize the value.
        float value = Math.pow(10, (0.05 * mValue)) > 0.05 ? 0.1f : 0;

        /// If we defined the current sound level as the amplitude of the wave, the wave would jitter very nervously.
        /// To avoid this, we use an inert amplitude that lifts slowly if the value is currently high, and damps itself
        /// if the value decreases.
        if (value > mDampingAmplitude) {
            mDampingAmplitude += (Math.min(value, 1.0) - mDampingAmplitude) / 4.0;
        } else if (value < 0.01) {
            mDampingAmplitude *= mDampingFactor;
        }

        mPhase += mPhaseShift;
        mAmplitude = Math.max(Math.min(mDampingAmplitude * 20, 1.0f), mIdleAmplitude);
    }
}
