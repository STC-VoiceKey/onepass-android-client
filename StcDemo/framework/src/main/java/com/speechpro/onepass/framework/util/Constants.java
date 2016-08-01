package com.speechpro.onepass.framework.util;

import android.media.AudioFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;

/**
 * @author volobuev
 * @since 16.03.16
 */
public final class Constants {

    // Old one -> "http://79.134.214.2:8080/vkonepass/rest/v4/";
    //"http://79.134.214.10:80/vkonepass/rest/v4/"
    public static final String URL = "http://sids:8080/vkonepass/rest/v4/";

    public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    public static final int SAMPLE_RATE  = 11025;
    public static final int CHANNELS     = AudioFormat.CHANNEL_IN_MONO;

    public static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;


    public static final int MAX_DURATION  = 7000; //5 second
    public static final int SUCCESS_FACES = 2;

    public static final int SUCCESS_VERIFICATION_FACES = 1;
    //Milliseconds
    public static final int ENROLLMENT_TIMEOUT         = 9000;
    public static final int VERIFICATION_TIMEOUT       = 7000;
    public static final int RESULT_DELAY               = 3000;
    public static final int RECORD_TICK                = 100;

    public static final int CANCEL_TIMEOUT = 180000;

    public static final int PERMISSIONS_RECORD_AUDIO           = 1;
    public static final int PERMISSIONS_CAMERA                 = 2;
    public static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3;
    public static final int PERMISSIONS_READ_EXTERNAL_STORAGE  = 4;
    public static final int PERMISSIONS                        = 5;

    public static final String ACTIVITY_RESULT = "result";
    public static final String SUCCES = "succes";
    public static final String FAIL = "fail";

    public static final int ENROLL_REQUEST_CODE = 1;
    public static final int VERIFY_REQUEST_CODE = 2;
}
