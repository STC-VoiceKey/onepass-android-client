package com.speechpro.onepass.framework.shadows;

import static org.assertj.core.api.Java6Assertions.assertThat;

import android.hardware.Camera;
import android.os.Build;

import com.speechpro.onepass.framework.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

/**
 * Created by alexander on 31.08.17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP_MR1, packageName = "com.speechpro.onepass.framework")
public class ShadowCameraSizeTest {

    private Camera.Size cameraSize;

    @Before
    public void setUp() throws Exception {
        cameraSize = Shadow.newInstanceOf(Camera.class).new Size(480, 320);
    }

    @Test
    public void testConstructor() throws Exception {
        assertThat(cameraSize.width).isEqualTo(480);
        assertThat(cameraSize.height).isEqualTo(320);
    }

    @Test
    public void testSetWidth() throws Exception {
        assertThat(cameraSize.width).isNotEqualTo(640);
        cameraSize.width = 640;
        assertThat(cameraSize.width).isEqualTo(640);
    }

    @Test
    public void testSetHeight() throws Exception {
        assertThat(cameraSize.height).isNotEqualTo(480);
        cameraSize.height = 480;
        assertThat(cameraSize.height).isEqualTo(480);
    }

}