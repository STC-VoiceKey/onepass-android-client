package com.speechpro.stcdemo.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

/**
 * Created by grigal on 03.05.2017.
 */

public class AppInfo {

    private final Context mContext;

    public AppInfo(Context ctx) {
        this.mContext = ctx;
    }

    @Nullable
    public String getVersionName() {
        String versionName = null;
        try {
            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }


    public int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

}
