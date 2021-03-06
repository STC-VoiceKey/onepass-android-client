package com.speechpro.onepass.framework.model;

import android.util.Pair;

import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;
import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.Video;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author volobuev
 * @since 17.02.2016
 */
public interface IModel {

    String startSession() throws InternetConnectionException, RestException;

    PersonSession readPerson(String personId) throws CoreException;

    void deletePerson(String personId);

    Boolean isFullEnroll(String personId);

    RegistrationTransaction startRegistrationTransaction(String personId);

    void addEnrollmentDynamicVoice(VoiceSample voiceSample) throws CoreException;

    void addEnrollmentStaticVoice(VoiceSample voiceSample) throws CoreException;

    void addEnrollmentFace(FaceSample faceSample) throws CoreException;

    VerificationTransaction startVerificationTransaction(String personId);

    String getVerificationPassphrase();

    void addVerificationDynamicVoice(VoiceSample voiceSample) throws CoreException;

    void addVerificationStaticVoice(VoiceSample voiceSample) throws CoreException;

    void addVerificationFace(FaceSample faceSample) throws CoreException;

    void addVerificationVideo(Video video) throws CoreException;

    Pair<Boolean, String> getVerificationResultWithMessage() throws CoreException;

    void deleteVerificationSession();

}
