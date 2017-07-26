package com.speechpro.onepass.framework.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * @author volobuev
 * @since 11.08.16
 */
public abstract class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);

        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
            return;
        }

        float ratio;
        if (mRatioHeight >=  mRatioWidth){
            ratio = (float) mRatioHeight / (float) mRatioWidth;
        } else {
            ratio = (float) mRatioWidth / (float) mRatioHeight;
        }

        float camHeight = width * ratio;
        float newCamHeight;
        float newHeightRatio;

        if(camHeight < height){
            newHeightRatio = (float) height / (float) mRatioHeight;
            newCamHeight = (newHeightRatio * camHeight);
            setMeasuredDimension((int)(width * newHeightRatio), (int) newCamHeight);
        } else {
            setMeasuredDimension(width, (int) camHeight);
        }

//
//        if (0 == mRatioWidth || 0 == mRatioHeight) {
//            setMeasuredDimension(width, height);
//        } else {
//            if (width < height * mRatioWidth / mRatioHeight)
//                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//            else
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//        }
    }

}
