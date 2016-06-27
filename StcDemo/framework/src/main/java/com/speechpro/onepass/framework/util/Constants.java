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

    public static final int    CANCEL_TIMEOUT = 180000;
    public static final String phraseDynamic1 = "zero one two three four five six seven eight nine";
    public static final String phraseDynamic2 = "nine eight seven six five four three two one zero";
    public static final String phraseDynamic3 = "one seven four zero nine three eight two five six";


    public static final int PERMISSIONS_RECORD_AUDIO           = 1;
    public static final int PERMISSIONS_CAMERA                 = 2;
    public static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3;
    public static final int PERMISSIONS_READ_EXTERNAL_STORAGE  = 4;
    public static final int PERMISSIONS                        = 5;
}
