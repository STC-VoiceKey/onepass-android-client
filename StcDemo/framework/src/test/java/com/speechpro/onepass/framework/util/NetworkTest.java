package com.speechpro.onepass.framework.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.speechpro.onepass.framework.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSettings;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexander on 13.09.17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP_MR1, packageName = "com.speechpro.onepass.framework")
public class NetworkTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void test_isAvailable__shouldReturnFalse() throws Exception {
        setConnectivity(false);
        boolean actual = Network.isAvailable(context);

        assertEquals(false, actual);
    }

    @Test
    public void test_isAvailable__shouldReturnTrue() throws Exception {
        setConnectivity(true);
        boolean actual = Network.isAvailable(context);

        assertEquals(true, actual);
    }

    private static void setConnectivity( boolean enabled) throws Exception {
        Context context = RuntimeEnvironment.application;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Shadows.shadowOf(wifiManager).setWifiEnabled(enabled);
        wifiManager = (WifiManager) RuntimeEnvironment.application.getSystemService(Context.WIFI_SERVICE);
        Shadows.shadowOf(wifiManager).setWifiEnabled(enabled);

        ConnectivityManager dataManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Shadows.shadowOf(dataManager).setBackgroundDataSetting(enabled);
        dataManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        Shadows.shadowOf(dataManager).setBackgroundDataSetting(enabled);

        Shadows.shadowOf(dataManager.getActiveNetworkInfo()).setConnectionStatus(enabled);

        Intent connIntent = new Intent(ConnectivityManager.CONNECTIVITY_ACTION);
        connIntent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, enabled);
        RuntimeEnvironment.application.sendBroadcast(connIntent);

        ShadowSettings shadowSettings = new ShadowSettings();
        shadowSettings.setWifiOn(enabled);
        shadowSettings.setAirplaneMode(enabled);
    }

}