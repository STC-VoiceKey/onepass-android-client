package com.speechpro.onepass.framework.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by grigal on 24.05.2017.
 */

public class AudioHelper {

    private static final String TAG = AudioHelper.class.getSimpleName();

    private final String SERVICECMD = "com.android.music.musicservicecommand";
    private final String CMDNAME = "command";
    private final String CMDPAUSE = "pause";
    private final String CMDPLAY = "play";

    private final AudioManager mAudioManager;
    private final Activity mActivity;
    private boolean mIsPaused;

    public AudioHelper(Activity activity) {
        mAudioManager = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
        this.mActivity = activity;
    }

    public void pauseOtherActivePlayer() {
        if(mAudioManager.isMusicActive()) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDPAUSE);
            mActivity.sendBroadcast(i);
            mIsPaused = true;
        }
    }

    public void playOtherActivePlayer() {
        if (!mAudioManager.isMusicActive() && mIsPaused) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDPLAY);
            mActivity.sendBroadcast(i);
            mIsPaused = false;
        }
    }

}
