package com.speechpro.onepass.framework.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;

/**
 * Created by grigal on 17.05.2017.
 */

public class DisplayUtils {

    private static final String TAG = DisplayUtils.class.getSimpleName();

    public static Pair<Integer, Integer> getDisplaySizeInPx(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Pair<>(metrics.widthPixels, metrics.heightPixels);
    }

    public static int getDisplayWidthInPx(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDisplayHeightInPx(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getDisplayDensityDpi(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }


    /**
     *
     * @param activity
     * @return
     *
     *     LDPI - 0.75x
     *     MDPI - 1.0x
     *     HDPI - 1.5x
     *     XHDPI - 2.0x
     *     XXHDPI - 3x
     *     XXXHDPI - 4.0x
     */
    public static float getScalingRatio(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int densityDpi = metrics.densityDpi;
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return 0.75f;
            case DisplayMetrics.DENSITY_MEDIUM:
                return 1f;
            case DisplayMetrics.DENSITY_HIGH:
                return 1.5f;
            case DisplayMetrics.DENSITY_XHIGH:
                return 2f;
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_420:
                return 3f;
            case DisplayMetrics.DENSITY_XXXHIGH:
            case DisplayMetrics.DENSITY_560:
                return 4f;
            default:
                return 1f;
        }
    }

}
