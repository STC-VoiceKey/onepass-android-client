package com.speechpro.onepass.framework.media;

import com.speechpro.onepass.framework.exceptions.RecorderException;



/**
 * @author volobuev
 * @since 14.06.16
 */
public abstract class Recorder {

    public abstract void startRecording();

    public abstract void stopRecording();

    public abstract byte[] getMedia() throws RecorderException;

    public abstract boolean isPrepared();
}
