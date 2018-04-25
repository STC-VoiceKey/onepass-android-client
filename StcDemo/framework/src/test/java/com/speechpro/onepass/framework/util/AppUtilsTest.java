package com.speechpro.onepass.framework.util;


import android.os.Build;

import com.speechpro.onepass.framework.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;
import static junit.framework.Assert.assertEquals;


/**
 * Created by alexander on 01.09.17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP_MR1, packageName = "com.speechpro.onepass.framework")
public class AppUtilsTest {

    @Test
    @Config(sdk = LOLLIPOP)
    public void onLolipop_hasRuntimePermissions_shouldReturnFalse() {

        boolean result = AppUtils.hasRuntimePermissions();

        assertEquals(false, result);
    }

    @Test
    @Config(sdk = M)
    public void onM_hasRuntimePermissions_shouldReturnTrue() {

        boolean result = AppUtils.hasRuntimePermissions();

        assertEquals(true, result);
    }

    @Test
    @Config(sdk = N)
    public void onN_hasRuntimePermissions_shouldReturnTrue() {

        boolean result = AppUtils.hasRuntimePermissions();

        assertEquals(true, result);
    }

}