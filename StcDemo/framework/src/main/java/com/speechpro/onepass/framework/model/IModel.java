package com.speechpro.onepass.framework.model;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.VerificationSession;
import com.speechpro.onepass.framework.model.data.FaceSample;
import com.speechpro.onepass.framework.model.data.Video;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author volobuev
 * @since 17.02.2016
 */
public interface IModel {

    PersonSession createPerson(String personId);

    PersonSession readPerson(String personId) throws CoreException;

    void deletePerson(String personId);

    Boolean isFullEnroll(String personId);

    void addEnrollmentVoice(VoiceSample voiceSample) throws CoreException;

    void addEnrollmentFace(FaceSample faceSample) throws CoreException;

    VerificationSession startVerification(String personId);

    void addVerificationVoice(VoiceSample voiceSample) throws CoreException;

    void addVerificationFace(FaceSample faceSample) throws CoreException;

    void addVerificationVideo(Video video) throws CoreException;

    Boolean getVerificationResult();

    void deleteVerificationSession();

}
