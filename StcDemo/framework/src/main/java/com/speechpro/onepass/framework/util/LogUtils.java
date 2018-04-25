package com.speechpro.onepass.framework.util;

import android.os.Environment;
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
public final class LogUtils {

    private static final String           TAG         = LogUtils.class.getSimpleName();
    private static final String           APP_STORAGE = Environment.getExternalStorageDirectory() + "/stc_demo/";
    private static final SimpleDateFormat SDF         = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

    private static boolean hasWriteToFile;

    public static void logFaces(byte[] value) {
        String facePath = "faces/face_" + SDF.format(new Date()) + ".jpg";
        writeFile(facePath, value);
        Log.d(TAG, "Face was saved in " + facePath);
    }

    public static void logVoice(byte[] value) {
        String sdf = SDF.format(new Date());
        String pcmPath = "pcm/pcm_" + sdf + ".pcm";
        String wavPath = "pcm/wav_" + sdf + ".wav";
        writeFile(pcmPath, value);

        Log.d(TAG, "PCM was saved in " + pcmPath);

        File pcmFile = new File(APP_STORAGE + pcmPath);
        File wavFile = new File(APP_STORAGE + wavPath);
        try {
            AudioConverter.rawToWave(pcmFile, wavFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "WAV was saved in " + wavFile);
    }

    public static void logVideo(byte[] value) {
        String videoPath = "video/video_" + SDF.format(new Date()) + ".mp4";
        writeFile(videoPath, value);
        Log.d(TAG, "Video was saved in " + videoPath);
    }

    public static void writeFile(String path, byte[] value) {
        if (!LogUtils.hasWriteToFile) return;

        try {
            File file   = new File(APP_STORAGE + path);
            File folder = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('/')));
            folder.mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(value);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Cannot write to file: ", e);
        }
    }

    public static void setWriteToLogFile(boolean hasWriteToFile) {
        LogUtils.hasWriteToFile = hasWriteToFile;
    }
}
