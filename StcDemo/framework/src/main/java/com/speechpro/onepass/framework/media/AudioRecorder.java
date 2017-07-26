package com.speechpro.onepass.framework.media;

import android.media.AudioRecord;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.media.AudioRecord.STATE_INITIALIZED;
import static com.speechpro.onepass.framework.util.Constants.AUDIO_ENCODING;
import static com.speechpro.onepass.framework.util.Constants.AUDIO_SOURCE;
import static com.speechpro.onepass.framework.util.Constants.CHANNELS;
import static com.speechpro.onepass.framework.util.Constants.SAMPLE_RATE;

/**
 * @author volobuev
 * @since 10.11.16
 */
public class AudioRecorder {

    private static final String TAG = AudioRecorder.class.getSimpleName();

    private static final int   THREAD_COUNT      = 3;
    private static final short BUFFER_SIZE_WRITE = 1024;
    private static final short SILENCE_APMLITUDE = 1000;
    private static final short SILENCE_LENGTH    = 2 * BUFFER_SIZE_WRITE / 3;
    private static final short SPEECH_LENGTH     = BUFFER_SIZE_WRITE / 3;

    private AudioRecord           mRecord;
    private AudioListener         mAudioListener;
    private ByteArrayOutputStream mPcm;
    private final int             mBufferSize;

    private ExecutorService mService = Executors.newFixedThreadPool(THREAD_COUNT);
    private Handler mHandler = new Handler();

    private volatile boolean mIsRecording;
    private volatile boolean isReadyAudioListener;

    public AudioRecorder(AudioListener mAudioListener) {
        this.mAudioListener = mAudioListener;
        this.mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS, AUDIO_ENCODING);
        Log.d(TAG, "Buffer size for recording is " + mBufferSize);
        mRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNELS, AUDIO_ENCODING, mBufferSize);

        if (mRecord.getState() == STATE_INITIALIZED) {
            Log.d(TAG, "AudioRecord is initialized.");
        } else {
            Log.e(TAG, "AudioRecord is not initialized!");
        }

        mService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Recording is starting...");
                try {
                    mRecord.startRecording();
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mIsRecording = true;
    }

    public void removeAudioListener() {
        this.mAudioListener = null;
    }

    public void start() {
        mPcm = new ByteArrayOutputStream();
        mService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Acoustostart is running...");
                write();
            }
        });
    }

    public void stop() {
        mIsRecording = false;
    }

    public void release() {
        mService.shutdown();
        Log.d(TAG, "AudioRecorder is releasing...");
        mRecord.release();
    }

    private short getShort(byte argB1, byte argB2) {
        return (short)(argB1 | (argB2 << 8));
    }

    private void write() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isReadyAudioListener = true;
            }
        }, 300);

        do {
            byte bData[] = new byte[mBufferSize];

            int mAmplitudeForChannel = 0;

            int read = mRecord.read(bData, 0, mBufferSize);

            for (int i = 0; i < read/2; i++) {
                short curSample = getShort(bData[i*2], bData[i*2+1]);
                if (curSample > mAmplitudeForChannel) {
                    mAmplitudeForChannel = curSample;
                }
            }

            if (mAudioListener != null && isReadyAudioListener) {
                mAudioListener.onProcess((short) mAmplitudeForChannel);
            }

            mPcm.write(bData, 0, mBufferSize);
        } while (mIsRecording);

        try {
            Log.d(TAG, "Audio processing....");
            if (mAudioListener != null)
                mAudioListener.stop(mPcm.toByteArray());
        } finally {
            Log.d(TAG, "Finalize...");
            mRecord.release();
            Log.d(TAG, "Recorder is stopped and released.");
            try {
                mPcm.close();
            } catch (IOException e) {
                Log.e(TAG, "PCM stream cannot be closed: ", e);
            }
        }
    }

    private byte[] short2byte(short[] sData) {
        int    shortArrSize = sData.length;
        byte[] bytes        = new byte[shortArrSize * 2];
        for (int i = 0; i < shortArrSize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private void logData(short[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Data = {");
        sb.append(data[0]);
        for (int i = 1; i < data.length; i++) {
            sb.append(", ").append(data[i]);
        }
        sb.append("}");
        Log.d(TAG, sb.toString());
    }
}
