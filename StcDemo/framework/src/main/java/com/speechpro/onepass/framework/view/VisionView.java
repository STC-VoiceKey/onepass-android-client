package com.speechpro.onepass.framework.view;

import android.content.Context;
import android.hardware.Camera;

/**
 * @author volobuev
 * @since 04.04.16
 */
public interface VisionView {

    /**
     * It processes image. In one case it takes a picture for enrolment,
     * in another it takes a video for verification.
     */
    void processVision();

    Camera getCamera();

    Context getContext();
}
