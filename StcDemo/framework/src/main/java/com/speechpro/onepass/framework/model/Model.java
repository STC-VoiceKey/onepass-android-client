package com.speechpro.onepass.framework.model;

import android.util.Log;
import android.util.Pair;

import com.speechpro.android.session.session_library.SessionClientFactory;
import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
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
import com.speechpro.onepass.framework.model.tasks.CreateSessionTask;
import com.speechpro.onepass.framework.model.tasks.DeletePersonTask;
import com.speechpro.onepass.framework.model.tasks.DeleteVerificationTransactionTask;
import com.speechpro.onepass.framework.model.tasks.EnrollmentFaceTask;
import com.speechpro.onepass.framework.model.tasks.EnrollmentDynamicVoiceTask;
import com.speechpro.onepass.framework.model.tasks.EnrollmentStaticVoiceTask;
import com.speechpro.onepass.framework.model.tasks.ExceptionAsyncTask;
import com.speechpro.onepass.framework.model.tasks.ReadPersonTask;
import com.speechpro.onepass.framework.model.tasks.StartRegistrationTransactionTask;
import com.speechpro.onepass.framework.model.tasks.StartVerificationTransactionTask;
import com.speechpro.onepass.framework.model.tasks.VerificationFaceTask;
import com.speechpro.onepass.framework.model.tasks.VerificationResultTask;
import com.speechpro.onepass.framework.model.tasks.VerificationStaticVoiceTask;
import com.speechpro.onepass.framework.model.tasks.VerificationVideoTask;
import com.speechpro.onepass.framework.model.tasks.VerificationDynamicVoiceTask;


/**
 * @author volobuev
 * @since 17.02.2016
 */
public class Model implements IModel {

    private final static String TAG = Model.class.getSimpleName();

    private final ITransport transport;
    private SessionClientFactory.SessionClient sessionClient;

    private String username;
    private String password;
    private int domainId;

    private RegistrationTransaction registrationTransaction;
    private VerificationTransaction verificationTransaction;

    private String sessionId;

    public Model() {
        this.transport = FrameworkInjection.getTransport();
    }

    public Model(String serverUrl, String sessionUrl, String username, String password, int domainId) {
        this.username = username;
        this.password = password;
        this.domainId = domainId;
        this.transport = new RetroRestAPI(serverUrl);
        this.sessionClient = SessionClientFactory.get(sessionUrl, true);
    }

    @Override
    public String startSession() throws InternetConnectionException, RestException {
        CreateSessionTask task = new CreateSessionTask(sessionClient, username, password, domainId);
        try {
            sessionId = task.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkRestExceptionException(task);
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
    public void addEnrollmentDynamicVoice(VoiceSample voiceSample) throws CoreException {
        EnrollmentDynamicVoiceTask task = new EnrollmentDynamicVoiceTask(registrationTransaction);
        try {
            task.execute(voiceSample).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }

    @Override
    public void addEnrollmentStaticVoice(VoiceSample voiceSample) throws CoreException {
        EnrollmentStaticVoiceTask task = new EnrollmentStaticVoiceTask(registrationTransaction);
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
    public void addVerificationDynamicVoice(VoiceSample voiceSample) throws CoreException {
        VerificationDynamicVoiceTask task = new VerificationDynamicVoiceTask(verificationTransaction);
        try {
            task.execute(voiceSample).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        checkException(task);
    }

    @Override
    public void addVerificationStaticVoice(VoiceSample voiceSample) throws CoreException {
        VerificationStaticVoiceTask task = new VerificationStaticVoiceTask(verificationTransaction);
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
    public Pair<Boolean, String> getVerificationResultWithMessage() throws CoreException {
        VerificationResultTask task = new VerificationResultTask(verificationTransaction);
        Pair<Boolean, String> res = null;
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
        if (exception instanceof ServiceUnavailableException) {
            throw exception;
        }
    }

    private void checkRestExceptionException(CreateSessionTask task) throws InternetConnectionException, RestException {
        Exception exception = task.getException();
        if (exception instanceof InternetConnectionException) {
            throw (InternetConnectionException) exception;
        } else if (exception instanceof RestException) {
            throw (RestException) exception;
        }
    }

}
