package com.speechpro.stcdemo.permission;

import java.util.ArrayList;

/**
 * Created by Alexander Grigal on 02.03.18.
 */

public interface PermissionCallback {

    void PermissionGranted(int requestCode);
    void PartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions);
    void PermissionDenied(int requestCode);
    void NeverAskAgain(int requestCode);

}
