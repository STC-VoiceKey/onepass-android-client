package com.speechpro.onepass.core.transport;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.VerificationSession;

/**
 * The interface which implements communication operation with server or local resources.
 * You can use all operations from person and verification scenario.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public interface ITransport {

    /**
     * Creates person with personId as id.
     *
     * @param personId person id.
     * @return person session
     * @throws CoreException is thrown when person already exist.
     */
    PersonSession createPerson(String personId) throws CoreException;

    /**
     * Retrieves person with personId.
     *
     * @param personId person id.
     * @return person session
     * @throws CoreException is thrown when person is not exist.
     */
    PersonSession readPerson(String personId) throws CoreException;

    /**
     * Deletes person.
     *
     * @param personId person id for deleting
     * @throws CoreException is thrown when person is not exist.
     */
    void deletePerson(String personId) throws CoreException;

    /**
     * Adds VoiceSample for the person.
     *
     * @param personId    person id for adding the VoiceSample
     * @param voiceSample byte array is containing the VoiceSample
     * @param passphrase  passphrase which was spoken by person
     * @throws CoreException is thrown when person is not exist.
     */
    void addVoiceSample(String personId, byte[] voiceSample, String passphrase, int samplingRate) throws CoreException;

    /**
     * Adds VoiceSample for the person with gender.
     *
     * @param personId    person id for adding the VoiceSample
     * @param voiceSample byte array is containing the VoiceSample
     * @param passphrase  passphrase which was spoken by person
     * @param gender      person gender: 0 is men, 1 is women
     * @throws CoreException is thrown when person is not exist.
     */
    void addVoiceSample(String personId, byte[] voiceSample, String passphrase, int gender, int samplingRate) throws CoreException;

    /**
     * Adds VoiceFeature for the person.
     *
     * @param personId     person id for adding the VoiceSample
     * @param voiceFeature byte array is containing the VoiceSample
     * @throws CoreException is thrown when person is not exist.
     */
    void addVoiceFeature(String personId, byte[] voiceFeature) throws CoreException;


    /**
     * Deletes voice for the person.
     *
     * @param personId person id for deleting the voicel
     * @throws CoreException is thrown when person is not exist.
     */
    void deleteVoice(String personId) throws CoreException;

    /**
     * Adds FaceModel for the person.
     *
     * @param personId  person id for adding the FaceModel
     * @param faceModel byte array is containing face model
     * @throws CoreException is thrown when person is not exist.
     */
    void addFaceModel(String personId, byte[] faceModel) throws CoreException;

    /**
     * Adds FaceSample for the person.
     *
     * @param personId   person id for adding the FaceModel
     * @param faceSample byte array is containing face sample
     * @throws CoreException is thrown when person is not exist.
     */
    void addFaceSample(String personId, byte[] faceSample) throws CoreException;

    /**
     * Deletes face for the person.
     *
     * @param personId person id for deleting the face
     * @throws CoreException is thrown when person is not exist.
     */
    void deleteFace(String personId) throws CoreException;

    /**
     * Starts verification session for the person.
     *
     * @param personId person id for verification session
     * @return verification session
     * @throws CoreException is thrown when person is not exist.
     */
    VerificationSession startVerificationSession(String personId) throws CoreException;

    /**
     * Adds VoiceSample for the verification with gender.
     *
     * @param session     current verification session
     * @param voiceSample byte array is containing the VoiceSample
     * @param gender      person gender: 0 is men, 1 is women
     * @throws CoreException is thrown when verification session is not exist.
     */
    void addVerificationVoiceSample(VerificationSession session, byte[] voiceSample, int gender, int samplingRate) throws CoreException;

    /**
     * Adds VoiceFeature for the verification with gender.
     *
     * @param session      current verification session
     * @param voiceFeature byte array is containing the VoiceFeature
     * @throws CoreException is thrown when verification session is not exist.
     */
    void addVerificationVoiceFeature(VerificationSession session, byte[] voiceFeature) throws CoreException;

    /**
     * Adds FaceModel for the verification.
     *
     * @param session   current verification session
     * @param faceModel byte array is containing the FaceModel
     * @throws CoreException is thrown when verification session is not exist.
     */
    void addVerificationFaceModel(VerificationSession session, byte[] faceModel) throws CoreException;

    /**
     * Adds FaceSample for the verification with gender.
     *
     * @param session    current verification session
     * @param faceSample byte array is containing the VoiceFeature
     * @throws CoreException is thrown when verification session is not exist.
     */
    void addVerificationFaceSample(VerificationSession session, byte[] faceSample) throws CoreException;

    /**
     * Adds Video to server.
     * This method may be used instead {@link #addVerificationFaceSample(VerificationSession, byte[])}
     * and {@link #addVerificationFaceModel(VerificationSession, byte[])}, but only it is used for liveness.
     *
     * @param session current verification session
     * @param video   byte array with video file
     * @throws CoreException
     */
    void addVerificationVideo(VerificationSession session, byte[] video) throws CoreException;

    /**
     * Checks liveness.
     *
     * @param session current verification session
     * @return true if person is alive, false - otherwise
     */
    boolean liveness(VerificationSession session) throws CoreException;

    /**
     * Verifies the person authenticity.
     *
     * @param session   current verification session
     * @param closeFlag flag for closing session
     * @return true if verification is success, false otherwise
     * @throws CoreException is thrown when verification session is not exist.
     */
    boolean verify(VerificationSession session, boolean closeFlag) throws CoreException;

    /**
     * Closes verification session.
     *
     * @param session current verification session
     * @throws CoreException is thrown when verification session is not exist.
     */
    void closeVerification(VerificationSession session) throws CoreException;
}
