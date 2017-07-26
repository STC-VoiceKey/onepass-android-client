package com.speechpro.onepass.framework.util;

import android.os.Build;

/**
 * Created by grigal on 29.06.2017.
 */

public class AppUtils {

    public static boolean hasRuntimePermissions() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }
}
