package com.speechpro.onepass.framework.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.speechpro.onepass.framework.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by grigal on 29.06.2017.
 */

public class RequestPermissions {

    public static final String REQUESTED_PERMISSION = "requested permission";

    public static final int REQUEST_PERMISSION_ALL = 0;

    private static IRequestPermissionListener mPermissionListener;

    public static void onRequestPermissionsResult(AppCompatActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissions.REQUEST_PERMISSION_ALL: {
                boolean granted = true;
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
                if (mPermissionListener != null) {
                    mPermissionListener.result(granted);
                }
            }
            break;

//
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkAllPermissions(IRequestPermissionListener listener, final Activity activity) {
        mPermissionListener = listener;

        List<String> permissionsList = new LinkedList<>();
        List<String> permissionsNeeded = new LinkedList<>();

        if (!addPermission(permissionsList, Manifest.permission.CAMERA, activity)) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO, activity)) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE, activity)) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = activity.getString(R.string.go_to_settings);
                showDialog(message, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPermissionListener != null) {
                            mPermissionListener.result(false);
                        }
                    }
                }, activity);
                return false;
            }
            final List<String> _permissionsList = permissionsList;
            activity.requestPermissions(_permissionsList.toArray(new String[_permissionsList.size()]),
                    RequestPermissions.REQUEST_PERMISSION_ALL);

            setFlagRequestedPermission(activity);
        } else if (permissionsList.size() == 0) {
            mPermissionListener.result(true);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean addPermission(List<String> permissionsList, String permission, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if ((!activity.shouldShowRequestPermissionRationale(permission))
                    && hasRequestedPermission(activity)) {
                return false;
            }
        }
        return true;
    }

    private static void showDialog(String message, DialogInterface.OnClickListener positiveListener, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        builder.setPositiveButton(R.string.ok, positiveListener);
        builder.setMessage(message);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean hasRequestedPermission(Context ctx) {
        SharedPreferences pref;
        pref = ctx.getSharedPreferences(REQUESTED_PERMISSION, Context.MODE_PRIVATE);
        if (pref.contains(REQUESTED_PERMISSION))
            return true;
        return false;
    }

    private static void setFlagRequestedPermission(Context ctx) {
        SharedPreferences pref;
        pref = ctx.getSharedPreferences(REQUESTED_PERMISSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(REQUESTED_PERMISSION, true);
        editor.apply();
    }
}
