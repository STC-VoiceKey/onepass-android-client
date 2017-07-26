package com.speechpro.onepass.framework.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @author volobuev
 * @since 17.08.16
 */
public final class DimensionUtils {

    /**
     * DP to PX converter
     *
     * @param context application context
     * @param dip     value in DP
     * @return value in PX
     */
    public static int convertDipToPixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                               dip,
                                               context.getResources().getDisplayMetrics());
    }

    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) { return min; }
        if (val.compareTo(max) > 0) { return max; }
        return val;
    }

}
