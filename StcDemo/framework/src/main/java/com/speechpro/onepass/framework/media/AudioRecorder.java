package com.speechpro.onepass.framework.media;

import android.media.AudioRecord;

import android.util.Log;
import com.speechpro.onepass.framework.exceptions.RecorderException;

import java.io.*;

import static com.speechpro.onepass.framework.util.Constants.*;


/**
 * @author volobuev
 * @since 16.03.16
 */
public class AudioRecorder extends Recorder {

    private static final String TAG = "AudioRecorder";

    private AudioRecord recorder;
    private Thread recordingThread;
    private boolean isRecording = false;
    private ByteArrayOutputStream pcm;

    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format

    @Override
    public void startRecording() {
        pcm = new ByteArrayOutputStream();
        recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNELS, AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        recorder.startRecording();

        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudio();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        Log.d(TAG, "Audio recording is started.");
    }

    @Override
    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
        Log.d(TAG, "Audio recording is stopped.");
    }

    @Override
    public byte[] getMedia() throws RecorderException {
        if (pcm != null) {
            byte[] res = pcm.toByteArray();
            return res;
        }
        throw new RecorderException("Cannot get audio data");
    }

    private void writeAudio() {
        short sData[] = new short[BufferElements2Rec];
        while (isRecording) {
            // gets the voice output from microphone to byte format

            int rs = recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("recorder status " + rs);

            // // writes the data to file from buffer
            // // stores the voice buffer
            byte bData[] = short2byte(sData);
            pcm.write(bData, 0, BufferElements2Rec * BytesPerElement);
        }
        try {
            pcm.flush();
            pcm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    @Override
    public boolean isPrepared(){
        return true;
    }
}
