package com.speechpro.stcdemo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

/**
 * Created by grigal on 03.05.2017.
 */

public class AppInfo {

    @Nullable
    public static String getVersionName(Context ctx) {
        String versionName = null;
        try {
            versionName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }


    public static int getVersionCode(Context ctx) {
        int versionCode = 0;
        try {
            versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

}
