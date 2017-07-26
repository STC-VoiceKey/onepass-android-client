package com.speechpro.onepass.framework.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.ui.listeners.RecButtonListener;

import java.lang.ref.WeakReference;

/**
 * Created by grigal on 11.05.2017.
 */

public class RecButtonView extends FrameLayout {

    private static final String TAG = RecButtonView.class.getSimpleName();

    private View mRootView;
    private int mProgressStatus;
    private int mMax;

    private WeakReference<ImageView> imageViewReference;
    private WeakReference<ProgressBar> progressReference;
    private WeakReference<RecButtonListener> recButtonListenerReference;

    private Handler mHandler = new Handler();
    private Thread mThread;

    private volatile boolean isRunning;

    public RecButtonView(@NonNull Context context) {
        super(context);
        init(null, 0);
        invalidateProgressbarAndImageView(context);
    }

    public RecButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
        invalidateProgressbarAndImageView(context);
    }

    public RecButtonView(@NonNull Context context, @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
        invalidateProgressbarAndImageView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecButtonView(@NonNull Context context, @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
        invalidateProgressbarAndImageView(context);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RecButtonView, defStyle, 0);

        mMax = a.getInteger(
                R.styleable.RecButtonView_max, 100);

        a.recycle();
    }

    private void invalidateProgressbarAndImageView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mRootView = layoutInflater.inflate(R.layout.mic_view, null);

        addView(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progressbar);
        progress.setVisibility(VISIBLE);
        progress.setProgress(0);
        progress.setMax(mMax);

        imageViewReference = new WeakReference<>(imageView);
        progressReference = new WeakReference<>(progress);
    }

    public void setRecButtonListener(RecButtonListener recButtonListener) {
        recButtonListenerReference = new WeakReference<>(recButtonListener);
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void startProgress() {
        if (mThread == null || !mThread.isAlive()) {

            if (imageViewReference.get() != null) {
                imageViewReference.get().setImageResource(R.drawable.ic_stop_white_48dp);
            }

            mThread = new Thread(new Runnable() {
                public void run() {
                    isRunning = true;
                    while (mProgressStatus < mMax && isRunning) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mProgressStatus++;
                        mHandler.post(new Runnable() {
                            public void run() {
                                if (progressReference.get() != null) {
                                    progressReference.get().setProgress(mProgressStatus);
                                }
                            }
                        });
                    }
                    if (mProgressStatus >= mMax || !isRunning()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (progressReference.get() != null && imageViewReference.get() != null) {
                                    progressReference.get().setProgress(0);
                                    imageViewReference.get().setImageResource(R.drawable.ic_mic_white_48dp);
                                }

                                if (recButtonListenerReference != null) {
                                    if (recButtonListenerReference.get() != null) {
                                        recButtonListenerReference.get().onProgressFinish();
                                    }
                                }
                            }
                        });

                        mProgressStatus = 0;
                        isRunning = false;
                    }
                }
            });

            mThread.start();
        }
    }

    public void stopProgress() {
        if (mThread != null && mThread.isAlive()) {
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
