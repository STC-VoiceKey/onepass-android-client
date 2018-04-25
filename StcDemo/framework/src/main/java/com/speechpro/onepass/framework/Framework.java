package com.speechpro.onepass.framework;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.framework.ui.view.camera.CameraQuality;
import com.speechpro.onepass.framework.model.IModel;
import com.speechpro.onepass.framework.model.Model;
import com.speechpro.onepass.framework.ui.activity.EnrollmentActivity;
import com.speechpro.onepass.framework.ui.activity.VerificationActivity;

import java.util.Set;
import java.util.UUID;

import static com.speechpro.onepass.framework.util.Constants.ENROLL_REQUEST_CODE;
import static com.speechpro.onepass.framework.util.Constants.VERIFY_REQUEST_CODE;

/**
 * @author volobuev
 * @since 21.04.16
 */
public final class Framework {

    private static final String TAG = Framework.class.getSimpleName();
//    private static final Logger LOG = LoggerFactory.getLogger(Framework.class);

    private static Framework instance;

    private IModel model;

    private String url;
    private String username;
    private String password;
    private int domainId;
    private boolean hasFace;
    private boolean hasVoice;
    private boolean hasLiveness;
    private boolean isDebugMode;
    private CameraQuality cameraQuality;

    private Framework(String url, String username, String password, int domainId,
                      boolean hasFace, boolean hasVoice, boolean hasLiveness,
                      boolean isDebugMode, CameraQuality cameraQuality) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.domainId = domainId;
        this.hasFace = hasFace;
        this.hasVoice = hasVoice;
        this.hasLiveness = hasLiveness;
        this.isDebugMode = isDebugMode;
        this.cameraQuality = cameraQuality;

        this.model = new Model(url, username, password, domainId);
    }

    public static Framework getFramework(String url, String username, String password, int domainId) {
        if (instance == null
                || !instance.url.equals(url)
                || !instance.username.equals(username)
                || !instance.password.equals(password)
                || instance.domainId != domainId) {
            instance = new Framework(url, username, password, domainId, true, true,
                    true, false, CameraQuality.LOW);
        }
        return instance;
    }

    public static Framework getFramework(String url, String username, String password, int domainId,
                                         boolean hasFace, boolean hasVoice, boolean hasLiveness,
                                         boolean isDebugMode, CameraQuality cameraQuality) {
        if (instance == null
                || !instance.url.equals(url)
                || !instance.username.equals(username)
                || !instance.password.equals(password)
                || instance.domainId != domainId
                || instance.hasFace != hasFace
                || instance.hasVoice != hasVoice
                || instance.hasLiveness != hasLiveness
                || instance.isDebugMode != isDebugMode
                || instance.cameraQuality != cameraQuality) {
            instance = new Framework(url, username, password, domainId, hasFace, hasVoice, hasLiveness,
                    isDebugMode, cameraQuality);
        }
        return instance;
    }

    public static Framework getInstance() {
        return instance;
    }

    public boolean isValidCredentials() throws CoreException {
        String sessionId = model.startSession();
        return sessionId != null && isValidUUID(sessionId);
    }

    public void startEnrollment(Activity activity, String userId) {
//        LOG.debug("Enrollment is started");
        activity.startActivityForResult(EnrollmentActivity.getCallingIntent(activity, userId,
                this.url, this.username, this.password, this.domainId,
                this.hasFace, this.hasVoice, this.hasLiveness,
                this.isDebugMode, this.cameraQuality), ENROLL_REQUEST_CODE);
    }

    public void startVerification(Activity activity, String userId) {
//        LOG.debug("Verification is started");
        activity.startActivityForResult(VerificationActivity.getCallingIntent(activity, userId,
                this.url, this.username, this.password, this.domainId,
                this.hasFace, this.hasVoice, this.hasLiveness,
                this.isDebugMode, cameraQuality), VERIFY_REQUEST_CODE);
    }

    public boolean isEnrolled(String userId, boolean flag) throws Exception {
        if (userId != null) {
            PersonSession session;
            try {
                String sessionId = model.startSession();
                if (sessionId == null) {
                    throw new Exception("Not valid session");
                }
                session = model.readPerson(userId);
            } catch (CoreException e) {
                throw new Exception(e);
            }
            if (session != null) {
                if (!isFullEnroll(session.getModels())) {
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
        com.speechpro.onepass.core.sessions.Model voiceModel = null;

        for (com.speechpro.onepass.core.sessions.Model model : models) {
            if (model.getType().equals("FACE_STC")) {
                faceModel = model;
            } else if (model.getType().equals("DYNAMIC_VOICE_KEY")) {
                voiceModel = model;
            }
        }

        if ((this.hasFace && this.hasVoice) || this.hasLiveness) {
            if ((faceModel != null && faceModel.getSamplesCount() > 0)
                    && (voiceModel != null && voiceModel.getSamplesCount() >= 3)) {
                return true;
            }
        } else if (this.hasFace) {
            if (faceModel != null && faceModel.getSamplesCount() > 0) {
                return true;
            }
        } else if (this.hasVoice) {
            if (voiceModel != null && voiceModel.getSamplesCount() >= 3) {
                return true;
            }
        }

        return false;
    }

    public boolean delete(String userId) throws Exception {
        PersonSession session;
        try {
            model.startSession();
            session = model.readPerson(userId);
        } catch (CoreException e) {
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
