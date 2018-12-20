package com.speechpro.onepass.framework;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Objects;
import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.TypeModel;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.Model;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.speechpro.onepass.framework.util.Constants.ENROLL_REQUEST_CODE;
import static com.speechpro.onepass.framework.util.Constants.VERIFY_REQUEST_CODE;

/**
 * @author Alexander Grigal
 */
public final class FrameworkFactory {

    private static final Map<Key, FrameworkFactory.Framework> store = new HashMap<>();

    public static final class Framework {
        private String serverURL;
        private String sessionURL;
        private String username;
        private String password;
        private Integer domainId;
        private Boolean hasDynamicVoice;
        private Boolean hasStaticVoice;
        private Boolean hasFace;
        private Boolean hasLiveness;
        private Boolean isDebugMode;
        private CameraQuality cameraQuality;

        private final IModel model;

        private Framework(String serverURL, String sessionURL,
                          String username, String password, Integer domainId,
                          Boolean hasDynamicVoice, Boolean hasStaticVoice, Boolean hasFace,
                          Boolean hasLiveness, Boolean isDebugMode, CameraQuality cameraQuality) {
            this.serverURL = serverURL;
            this.sessionURL = sessionURL;
            this.username = username;
            this.password = password;
            this.domainId = domainId;
            this.hasDynamicVoice = hasDynamicVoice;
            this.hasStaticVoice = hasStaticVoice;
            this.hasFace = hasFace;
            this.hasLiveness = hasLiveness;
            this.isDebugMode = isDebugMode;
            this.cameraQuality = cameraQuality;

            this.model = new Model(serverURL, sessionURL, username, password, domainId);
        }

        public boolean isValidCredentials() throws InternetConnectionException, RestException {
            String sessionId = model.startSession();
            return sessionId != null && isValidUUID(sessionId);
        }

        public void startEnrollment(Activity activity, String userId) {
            activity.startActivityForResult(EnrollmentActivity.getCallingIntent(activity, userId,
                    serverURL, sessionURL, username, password, domainId,
                    hasFace, hasDynamicVoice, hasStaticVoice, hasLiveness,
                    isDebugMode, cameraQuality), ENROLL_REQUEST_CODE);
        }

        public void startVerification(Activity activity, String userId) {
            activity.startActivityForResult(VerificationActivity.getCallingIntent(activity, userId,
                    serverURL, sessionURL, username, password, domainId,
                    hasFace, hasDynamicVoice, hasStaticVoice, hasLiveness,
                    isDebugMode, cameraQuality), VERIFY_REQUEST_CODE);
        }

        public boolean isEnrolled(String userId, boolean flag) throws Exception {
            if (userId != null) {
                PersonSession personSession;
                try {
                    model.startSession();
                    personSession = model.readPerson(userId);
                } catch (InternetConnectionException | RestException | CoreException e) {
                    throw new Exception(e);
                }
                if (personSession != null) {
                    if (!isFullEnroll(personSession.getModels())) {
                        if (flag) {
                            model.deletePerson(userId);
                        }
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean isFullEnroll(Set<com.speechpro.onepass.core.sessions.Model> models) {
            com.speechpro.onepass.core.sessions.Model faceModel = null;
            com.speechpro.onepass.core.sessions.Model dynamicVoiceModel = null;
            com.speechpro.onepass.core.sessions.Model staticVoiceModel = null;

            for (com.speechpro.onepass.core.sessions.Model model : models) {
                switch (model.getType()) {
                    case FACE_STC:
                        faceModel = model;
                        break;
                    case DYNAMIC_VOICE_KEY:
                        dynamicVoiceModel = model;
                        break;
                    case STATIC_VOICE_KEY:
                        staticVoiceModel = model;
                        break;
                }
            }

            if (hasFace && hasDynamicVoice) {
                return (faceModel != null && faceModel.getSamplesCount() > 0)
                        && (dynamicVoiceModel != null && dynamicVoiceModel.getSamplesCount() >= 3);
            } else if (hasFace && hasStaticVoice) {
                return (faceModel != null && faceModel.getSamplesCount() > 0)
                        && (staticVoiceModel != null && staticVoiceModel.getSamplesCount() >= 3);
            } else if (hasFace) {
                return faceModel != null && faceModel.getSamplesCount() > 0;
            } else if (hasDynamicVoice) {
                return dynamicVoiceModel != null && dynamicVoiceModel.getSamplesCount() >= 3;
            } else if (hasStaticVoice) {
                return staticVoiceModel != null && staticVoiceModel.getSamplesCount() >= 3;
            }

            return false;
        }

        public boolean delete(String userId) throws Exception {
            PersonSession session;
            try {
                model.startSession();
                session = model.readPerson(userId);
            } catch (InternetConnectionException | RestException | CoreException e) {
                throw new Exception(e);
            }
            if (session != null) {
                model.deletePerson(userId);
                return true;
            }
            return false;
        }

        private boolean isValidUUID(@NonNull String sessionId) {
            try {
                UUID uuid = UUID.fromString(sessionId);
                return true;
            } catch (IllegalArgumentException exception) {
                return false;
            }
        }

    }

    public static Framework get(String serverURL, String sessionURL,
                                String username, String password, Integer domainId,
                                Boolean hasDynamicVoice, Boolean hasStaticVoice, Boolean hasFace,
                                Boolean hasLiveness, Boolean isDebugMode, CameraQuality cameraQuality) {
        synchronized (store) {
            Key key = new Key(serverURL, sessionURL, username, password, domainId, hasDynamicVoice,
                    hasStaticVoice, hasFace, hasLiveness, isDebugMode, cameraQuality);
            FrameworkFactory.Framework result = store.get(key);
            if (result == null) {
                result = new Framework(serverURL, sessionURL, username, password, domainId,
                        hasDynamicVoice, hasStaticVoice, hasFace, hasLiveness, isDebugMode, cameraQuality);
                store.put(key, result);
            }
            return result;
        }
    }

    private static class Key {
        private String serverURL;
        private String sessionURL;
        private String username;
        private String password;
        private Integer domainId;
        private Boolean hasDynamicVoice;
        private Boolean hasStaticVoice;
        private Boolean hasFace;
        private Boolean hasLiveness;
        private Boolean isDebugMode;
        private CameraQuality cameraQuality;

        Key(String serverURL, String sessionURL, String username, String password, Integer domainId,
            Boolean hasDynamicVoice, Boolean hasStaticVoice, Boolean hasFace, Boolean hasLiveness,
            Boolean isDebugMode, CameraQuality cameraQuality) {
            this.serverURL = serverURL;
            this.sessionURL = sessionURL;
            this.username = username;
            this.password = password;
            this.domainId = domainId;
            this.hasDynamicVoice = hasDynamicVoice;
            this.hasStaticVoice = hasStaticVoice;
            this.hasFace = hasFace;
            this.hasLiveness = hasLiveness;
            this.isDebugMode = isDebugMode;
            this.cameraQuality = cameraQuality;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equal(serverURL, key.serverURL) &&
                    Objects.equal(sessionURL, key.sessionURL) &&
                    Objects.equal(username, key.username) &&
                    Objects.equal(password, key.password) &&
                    Objects.equal(domainId, key.domainId) &&
                    Objects.equal(hasDynamicVoice, key.hasDynamicVoice) &&
                    Objects.equal(hasStaticVoice, key.hasStaticVoice) &&
                    Objects.equal(hasFace, key.hasFace) &&
                    Objects.equal(hasLiveness, key.hasLiveness) &&
                    Objects.equal(isDebugMode, key.isDebugMode) &&
                    cameraQuality == key.cameraQuality;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(serverURL, sessionURL, username, password, domainId,
                    hasDynamicVoice, hasStaticVoice, hasFace, hasLiveness, isDebugMode, cameraQuality);
        }

    }
}
