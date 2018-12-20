package com.speechpro.onepass.framework.ui.fragment.verify;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.framework.R;
import com.speechpro.onepass.framework.presenter.BasePresenter;
import com.speechpro.onepass.framework.ui.activity.BaseActivity;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;
import com.speechpro.onepass.framework.ui.fragment.BaseFragment;
import com.speechpro.onepass.framework.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author volobuev
 * @since 27.04.16
 */
public class VerifyResultFragment extends BaseFragment {

    private static final String TAG = VerifyResultFragment.class.getName();

    private RelativeLayout mRelativeLayout;
    private RelativeLayout mGrantedLayout;
    private RelativeLayout mDeniedLayout;

    private BaseActivity  mActivity;
    private BasePresenter mPresenter;

    private ImageView mGreen1;
    private ImageView mGreen2;
    private ImageView mGreen3;
    private ImageView mGreen4;
    private ImageView mGreen5;

    private ImageView mRed1;
    private ImageView mRed2;
    private ImageView mRed3;
    private ImageView mRed4;
    private ImageView mRed5;

    private TextView mTextView;

    private AnimatorSet mAnimatorSet;

    private final Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_result, container, false);

        mActivity = (VerificationActivity) getActivity();
        mPresenter = mActivity.getPresenter();

        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.result_layout);
        mGrantedLayout = (RelativeLayout) view.findViewById(R.id.granted_layout);
        mDeniedLayout = (RelativeLayout) view.findViewById(R.id.denied_layout);

        mGreen1 = (ImageView) view.findViewById(R.id.green1);
        mGreen2 = (ImageView) view.findViewById(R.id.green2);
        mGreen3 = (ImageView) view.findViewById(R.id.green3);
        mGreen4 = (ImageView) view.findViewById(R.id.green4);
        mGreen5 = (ImageView) view.findViewById(R.id.green5);

        mRed1 = (ImageView) view.findViewById(R.id.red1);
        mRed2 = (ImageView) view.findViewById(R.id.red2);
        mRed3 = (ImageView) view.findViewById(R.id.red3);
        mRed4 = (ImageView) view.findViewById(R.id.red4);
        mRed5 = (ImageView) view.findViewById(R.id.red5);

        mTextView = (TextView) view.findViewById(R.id.result_tv);

        initialize();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAnimatorSet.start();
    }

    public void initialize() {
        try {
            Log.d(TAG, "Animation initializing...");
            Pair<Boolean, String> result = mPresenter.getResultWithMessage();
            if (result.first) {
                mGrantedLayout.setVisibility(View.VISIBLE);
                mDeniedLayout.setVisibility(View.GONE);
                mAnimatorSet = animation(mGreen1, mGreen2, mGreen3, mGreen4, mGreen5);
            } else {
                mGrantedLayout.setVisibility(View.GONE);
                mDeniedLayout.setVisibility(View.VISIBLE);
                mAnimatorSet = animation(mRed1, mRed2, mRed3, mRed4, mRed5);
            }
            if (mActivity.isDebugMode()) {
                mTextView.setText(result.second);
            }
            mAnimatorSet.setStartDelay(100);
            Log.d(TAG, "Animation initializing is finished");
        } catch (CoreException e) {
        }

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.nextEpisode();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.nextEpisode();
            }
        }, Constants.RESULT_DELAY);
    }

    private AnimatorSet animation(View circle1, View circle2, View circle3, View circle4, View circle5) {
        AnimatorSet    animator     = new AnimatorSet();
        List<Animator> animatorList = new ArrayList<>();

        ObjectAnimator animScaleX1 = ObjectAnimator.ofFloat(circle1, View.SCALE_X, 0.8f, 1f).setDuration(500);
        ObjectAnimator animScaleY1 = ObjectAnimator.ofFloat(circle1, View.SCALE_Y, 0.8f, 1f).setDuration(500);
        animatorList.add(animScaleX1);
        animatorList.add(animScaleY1);

        ObjectAnimator animScaleX2 = ObjectAnimator.ofFloat(circle2, View.SCALE_X, 0.8f, 1f).setDuration(500);
        ObjectAnimator animScaleY2 = ObjectAnimator.ofFloat(circle2, View.SCALE_Y, 0.8f, 1f).setDuration(500);
        animScaleX2.setStartDelay(25);
        animScaleY2.setStartDelay(25);
        animatorList.add(animScaleX2);
        animatorList.add(animScaleY2);

        ObjectAnimator animScaleX3 = ObjectAnimator.ofFloat(circle3, View.SCALE_X, 0.8f, 1f).setDuration(500);
        ObjectAnimator animScaleY3 = ObjectAnimator.ofFloat(circle3, View.SCALE_Y, 0.8f, 1f).setDuration(500);
        animScaleX2.setStartDelay(50);
        animScaleY2.setStartDelay(50);
        animatorList.add(animScaleX3);
        animatorList.add(animScaleY3);

        ObjectAnimator animScaleX4 = ObjectAnimator.ofFloat(circle4, View.SCALE_X, 0.8f, 1f).setDuration(500);
        ObjectAnimator animScaleY4 = ObjectAnimator.ofFloat(circle4, View.SCALE_Y, 0.8f, 1f).setDuration(500);
        ObjectAnimator animAlpha4  = ObjectAnimator.ofFloat(circle5, View.ALPHA, 1f, 0.8f).setDuration(500);
        animScaleX2.setStartDelay(75);
        animScaleY2.setStartDelay(75);
        animAlpha4.setStartDelay(75);
        animatorList.add(animScaleX4);
        animatorList.add(animScaleY4);
        animatorList.add(animAlpha4);

        ObjectAnimator animScaleX5 = ObjectAnimator.ofFloat(circle5, View.SCALE_X, 0.8f, 1f).setDuration(500);
        ObjectAnimator animScaleY5 = ObjectAnimator.ofFloat(circle5, View.SCALE_Y, 0.8f, 1f).setDuration(500);
        ObjectAnimator animAlpha5  = ObjectAnimator.ofFloat(circle5, View.ALPHA, 1f, 0.5f).setDuration(500);
        animScaleX2.setStartDelay(100);
        animScaleY2.setStartDelay(100);
        animAlpha5.setStartDelay(100);
        animatorList.add(animScaleX5);
        animatorList.add(animScaleY5);
        animatorList.add(animAlpha5);

        animator.playTogether(animatorList);
        return animator;
    }
}
