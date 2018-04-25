package com.speechpro.stcdemo.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;
import com.speechpro.stcdemo.R;
import com.speechpro.stcdemo.settings.server.ServerSettingsCredentials;

import java.io.IOException;

/**
 * Created by Alexander Grigal on 24.01.18.
 */

public class SharedPref {

    private final String APP_PREF_NAME = "pref_utils";
    private final String SERVER_CREDENTIALS = "server_credentials";
    private final String LOGIN = "login";
    private final String FACE = "face";
    private final String VOICE = "voice";
    private final String LIVENESS = "liveness";
    private final String DEBUG_MODE = "debug_mode";
    private final String CAMERA_QUALITY = "camera_quality";

    private final Context mContext;

    public SharedPref(Context ctx) {
        mContext = ctx;
    }

    public void setServerCredentials(@NonNull ServerSettingsCredentials credentials) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (jsonInString != null) {
            SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(SERVER_CREDENTIALS, jsonInString).commit();
        }
    }

    @NonNull
    public ServerSettingsCredentials getServerCredentials() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        String jsonInString = pref.getString(SERVER_CREDENTIALS, null);

        ServerSettingsCredentials result = null;

        if (jsonInString != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                result = mapper.readValue(jsonInString, ServerSettingsCredentials.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return new ServerSettingsCredentials(mContext.getString(R.string.url_vkopdm),
                    mContext.getString(R.string.username),
                    mContext.getString(R.string.password),
                    mContext.getString(R.string.domain_id));
        }

        return result;
    }

    public void setLogin(String login) {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LOGIN, login).commit();
    }

    public String getLogin() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        return pref.getString(LOGIN, "");
    }

    public void setFace(boolean hasFace) {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FACE, hasFace).commit();
    }

    public boolean hasFace() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        return pref.getBoolean(FACE, true);
    }

    public void setVoice(boolean hasVoice) {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(VOICE, hasVoice).commit();
    }

    public boolean hasVoice() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        return pref.getBoolean(VOICE, true);
    }

    public void setLiveness(boolean hasLiveness) {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(LIVENESS, hasLiveness).commit();
    }

    public boolean hasLiveness() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        return pref.getBoolean(LIVENESS, true);
    }

    public void setDebugMode(boolean isDebugMode) {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(DEBUG_MODE, isDebugMode).commit();
    }

    public boolean isDebugMode() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        return pref.getBoolean(DEBUG_MODE, false);
    }

    public void setCameraQuality(CameraQuality cameraQuality) {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(CAMERA_QUALITY, cameraQuality == CameraQuality.MEDIUM).commit();
    }

    public CameraQuality getCameraQuality() {
        SharedPreferences pref = mContext.getSharedPreferences(APP_PREF_NAME, 0);
        boolean value = pref.getBoolean(CAMERA_QUALITY, false);
        return value ? CameraQuality.MEDIUM : CameraQuality.LOW;
    }

}
