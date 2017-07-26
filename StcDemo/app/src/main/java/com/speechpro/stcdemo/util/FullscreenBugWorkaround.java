package com.speechpro.stcdemo.util;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by grigal on 07.06.2017.
 */

//when the application uses full screen theme and the keyboard is shown the content not scrollable!
//with this util it will be scrollable once again
//http://stackoverflow.com/questions/7417123/android-how-to-adjust-layout-in-full-screen-mode-when-softkeyboard-is-visible

public class FullscreenBugWorkaround {

    private static FullscreenBugWorkaround mInstance = null;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private ViewTreeObserver.OnGlobalLayoutListener _globalListener;

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static FullscreenBugWorkaround getInstance (Activity activity) {
        if(mInstance == null) {
            synchronized (FullscreenBugWorkaround.class) {
                mInstance = new FullscreenBugWorkaround(activity);
            }
        }
        return mInstance;
    }

    private FullscreenBugWorkaround(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();

        _globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        };
    }

    public void setListener() {
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(_globalListener);
    }

    public void removeListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(_globalListener);
        } else {
            mChildOfContent.getViewTreeObserver().removeGlobalOnLayoutListener(_globalListener);
        }
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

}
