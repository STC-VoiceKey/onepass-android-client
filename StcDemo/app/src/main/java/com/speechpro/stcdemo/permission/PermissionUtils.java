package com.speechpro.stcdemo.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.speechpro.stcdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander Grigal on 02.03.18.
 */

public class PermissionUtils {

    Context mContext;
    Activity mActivity;

    PermissionCallback mPermissionCallback;

    ArrayList<String> permission_list = new ArrayList<>();
    ArrayList<String> listPermissionsNeeded = new ArrayList<>();
    String dialogContent = "";
    int reqCode;

    public PermissionUtils(Activity activity) {
        this.mContext = activity;
        this.mActivity = activity;

        mPermissionCallback = (PermissionCallback) activity;
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialogContent
     * @param requestCode
     */

    public void checkPermission(ArrayList<String> permissions, String dialogContent, int requestCode) {
        this.permission_list = permissions;
        this.dialogContent = dialogContent;
        this.reqCode = requestCode;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, requestCode)) {
                mPermissionCallback.PermissionGranted(requestCode);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        } else {
            mPermissionCallback.PermissionGranted(requestCode);

            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }

    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param requestCode
     * @return
     */

    private boolean checkAndRequestPermissions(ArrayList<String> permissions, int requestCode) {

        if (permissions.size() > 0) {
            listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(mActivity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(mActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
                return false;
            }
        }

        return true;
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pendingPermissions = new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, listPermissionsNeeded.get(i)))
                                pendingPermissions.add(listPermissionsNeeded.get(i));
                            else {
                                Log.i("Go to settings", "and enable permissions");
                                mPermissionCallback.NeverAskAgain(reqCode);
                                Toast.makeText(mActivity, R.string.go_to_settings, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if (pendingPermissions.size() > 0) {
                        showMessageOKCancel(dialogContent,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkPermission(permission_list, dialogContent, reqCode);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i("permisson", "not fully given");
                                                if (permission_list.size() == pendingPermissions.size())
                                                    mPermissionCallback.PermissionDenied(reqCode);
                                                else
                                                    mPermissionCallback.PartialPermissionGranted(reqCode, pendingPermissions);
                                                break;
                                        }


                                    }
                                });

                    } else {
                        Log.i("all", "permissions granted");
                        Log.i("proceed", "to next step");
                        mPermissionCallback.PermissionGranted(reqCode);
                    }

                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, okListener)
                .create()
                .show();
    }

}
