package com.speechpro.onepass.framework.model;

import android.util.Log;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.exception.ServiceUnavailableException;
import com.speechpro.onepass.core.rest.api.RetroRestAPI;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.framework.injection.FrameworkInjection;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.Video;
import com.speechpro.onepass.framework.model.data.VoiceSample;
import com.speechpro.onepass.framework.model.tasks.*;


/**
 * @author volobuev
 * @since 17.02.2016
 */
public class Model implements IModel {

    private final static String TAG = "Model";

    private final ITransport transport;

    private final String username = "admin";
    private final String password = "QL0AFWMIX8NRZTKeof9cXsvbvu8=";
    private final int domainId = 201;

    private RegistrationTransaction registrationTransaction;
    private VerificationTransaction verificationTransaction;

    private String sessionId;

    public Model() {
        this.transport = FrameworkInjection.getTransport();
    }

    public Model(String url) {
        transport = new RetroRestAPI(url);
    }

    @Override
    public String startSession() throws CoreException {
        CreateSessionTask task = new CreateSessionTask(transport, username, password, domainId);
        try {
            sessionId = task.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkNetwork(task);
        return sessionId;
    }

    @Override
    public PersonSession readPerson(String personId) throws CoreException {
        ReadPersonTask task = new ReadPersonTask(transport, sessionId, personId);
        PersonSession result = null;
        try {
            result = task.execute().get();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        checkNetwork(task);
        return result;
    }

    @Override
    public void deletePerson(String personId) {
        try {
            new DeletePersonTask(transport).execute(sessionId, personId).get();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    @Override
    public Boolean isFullEnroll(String personId) {
        PersonSession session = null;
        try {
            session = readPerson(personId);
        } catch (CoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return session != null && session.isFullEnroll();
    }

    @Override
    public RegistrationTransaction startRegistrationTransaction(String personId) {
        try {
            registrationTransaction = new StartRegistrationTransactionTask(transport)
                    .execute(sessionId, personId).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return registrationTransaction;
    }


    @Override
    public void addEnrollmentVoice(VoiceSample voiceSample) throws CoreException {
        EnrollmentVoiceTask task = new EnrollmentVoiceTask(registrationTransaction);
        try {
            task.execute(voiceSample).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }

    @Override
    public void addEnrollmentFace(FaceSample faceSample) throws CoreException {
        EnrollmentFaceTask task = new EnrollmentFaceTask(registrationTransaction);
        try {
            task.execute(faceSample).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }

    @Override
    public VerificationTransaction startVerificationTransaction(String personId) {
        try {
            verificationTransaction = new StartVerificationTransactionTask(transport)
                    .execute(sessionId, personId).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return verificationTransaction;
    }

    @Override
    public String getVerificationPassphrase() {
        return verificationTransaction != null
                ? verificationTransaction.getPassphrase()
                : null;
    }

    @Override
    public void addVerificationVoice(VoiceSample voiceSample) throws CoreException {
        VerificationVoiceTask task = new VerificationVoiceTask(verificationTransaction);
        try {
            task.execute(voiceSample).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }

    @Override
    public void addVerificationFace(FaceSample faceSample) throws CoreException {
        VerificationFaceTask task = new VerificationFaceTask(verificationTransaction);
        try {
            task.execute(faceSample).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }

    @Override
    public void addVerificationVideo(Video video) throws CoreException {
        VerificationVideoTask task = new VerificationVideoTask(verificationTransaction);
        try {
            task.execute(video).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }


    @Override
    public Boolean getVerificationResult() throws CoreException {
        VerificationResultTask task = new VerificationResultTask(verificationTransaction);
        Boolean res = false;
        try {
            res = task.execute().get();
            checkNetwork(task);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            if (e instanceof ServiceUnavailableException) {
                throw (CoreException) e;
            }
        }
        return res;
    }

    @Override
    public void deleteVerificationSession() {
        try {
            new DeleteVerificationTransactionTask(verificationTransaction).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void checkException(ExceptionAsyncTask task) throws CoreException {
        CoreException exception = task.getException();
        if (exception != null) {
            throw exception;
        }
    }

    private void checkNetwork(ExceptionAsyncTask task) throws CoreException {
        CoreException exception = task.getException();
        if (exception != null && exception instanceof ServiceUnavailableException) {
            throw exception;
        }
    }

}
