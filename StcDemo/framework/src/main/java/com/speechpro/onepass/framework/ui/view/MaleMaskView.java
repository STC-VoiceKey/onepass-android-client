package com.speechpro.onepass.framework.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
import android.widget.TextView;

import com.speechpro.onepass.framework.R;

/**
 * Created by grigal on 03.05.2017.
 */

public class MaleMaskView extends FrameLayout {

    private static final String TAG = MaleMaskView.class.getSimpleName();

    private View mRootView;
    private TextView mTitle;
    private ImageView mImageView;


    public MaleMaskView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MaleMaskView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaleMaskView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaleMaskView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mRootView = layoutInflater.inflate(R.layout.male_mask_view, null);

        addView(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTitle = (TextView) findViewById(R.id.title_text);
        mImageView = (ImageView) findViewById(R.id.image);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mRootView = layoutInflater.inflate(R.layout.male_mask_view, null);

        addView(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTitle = (TextView) findViewById(R.id.title_text);
        mImageView = (ImageView) findViewById(R.id.image);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int height = getMeasuredHeight();
        final int width = getMeasuredWidth();

        setMeasuredDimension(width, height);
    }

}
