package com.speechpro.onepass.framework.util;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author volobuev
 * @since 25.03.16
 */
public final class Util {

    private static final String TAG = "AudioRecorder";
    private static final String APP_STORAGE = Environment.getExternalStorageDirectory() + "/stc_demo/";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0;
    }

    public static void logFaces(byte[] value) {
        String facePath = "faces/face_" + SDF.format(new Date()) + ".jpg";
        writeFile(facePath, value);
        Log.d(TAG, "Face was saved in " + facePath);
    }

    public static void logPcm(byte[] value){
        String pcmPath = "pcm/pcm_" + SDF.format(new Date()) + ".pcm";
        writeFile(pcmPath, value);
        Log.d(TAG,  "PCM was saved in " + pcmPath);
    }

    public static void logVideo(byte[] value){
        String videoPath = "video/video_" + SDF.format(new Date()) + ".3gp";
        writeFile(videoPath, value);
        Log.d(TAG,  "Video was saved in " + videoPath);
    }


    public static void writeFile(String path, byte[] value) {
        try {
            File file = new File(APP_STORAGE + path);
            File folder = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('/')));
            folder.mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(value);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Cannot write to file: ", e);
        }
    }
}
