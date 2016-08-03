package com.speechpro.onepass.framework.media;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import com.speechpro.onepass.framework.exceptions.RecorderException;
import com.speechpro.onepass.framework.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static android.media.MediaRecorder.AudioEncoder.AAC;
import static android.media.MediaRecorder.OutputFormat.THREE_GPP;
import static android.media.MediaRecorder.VideoEncoder.H264;
import static com.speechpro.onepass.framework.util.Constants.*;

/**
 * @author volobuev
 * @since 29.03.16
 */
public class VideoRecorder extends Recorder {

    private static final String TAG = "VideoRecorder";

    private MediaRecorder recorder;
    private boolean isRecording = false;
    private boolean isPrepared = false;

    private final Camera camera;

    private final String videoPath;

    public VideoRecorder(Camera camera, Context context) {
        this.camera = camera;
        this.videoPath = context.getFilesDir().getAbsolutePath().concat("/video.mp4");
        this.recorder = new MediaRecorder();
        isPrepared = prepare();
    }

    @Override
    public void startRecording() {
        new MediaPrepareTask().execute();
    }

    @Override
    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            try {
                recorder.stop();
            } catch (RuntimeException stopException) {
                releaseMediaRecorder();
            }
        }
    }

    @Override
    public byte[] getMedia() throws RecorderException {
        return getVideo();
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    private boolean prepare() {

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        profile.videoFrameWidth = 320;
        profile.videoFrameHeight = 240;
        profile.fileFormat = THREE_GPP;
        profile.videoCodec = H264;
        profile.audioCodec = AAC;
        profile.audioSampleRate = 11025;
        profile.audioChannels = 1;
        profile.videoFrameRate = 30;
        profile.videoBitRate = 500000;

        camera.unlock();

        int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOrientationHint(info.orientation);
        recorder.setProfile(profile);
        recorder.setOutputFile(videoPath);
        recorder.setMaxDuration(MAX_DURATION);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "IOException preparing MediaRecorder: ", e);
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private byte[] getVideo() throws RecorderException {
        if (!isRecording) {
            File file = new File(videoPath);
            FileInputStream fileInputStream;
            byte[] video = new byte[(int) file.length()];
            try {
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(video);
                fileInputStream.close();
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                new RecorderException("Cannot get video");
            }
            Util.logVideo(video);
            return video;
        }
        throw new RecorderException("Cannot get video");
    }

    private void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
            camera.lock();
        }
        deleteVideo();
    }

    private boolean deleteVideo() {
        return new File(videoPath).delete();
    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (isPrepared) {
                recorder.start();
                isRecording = true;
            }
            return isRecording;
        }

    }
}
