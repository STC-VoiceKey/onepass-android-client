package com.speechpro.onepass.core.transport;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.ScoreVerify;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;

/**
 * The interface which implements communication operation with server or local resources.
 * You can use all operations from person and verification scenario.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public interface ITransport {

    /**
     *
     * Open session
     *
     * @param username
     * @param password
     * @param domainId
     * @return person session
     * @throws CoreException
     */
    String startSession(String username,
                        String password,
                        int domainId) throws CoreException;

    /**
     *
     * Close active session
     *
     * @param sessionId
     * @throws CoreException
     */
    void closeSession(String sessionId) throws CoreException;

    /**
     *
     * Open registration transaction
     *
     * @param sessionId
     * @param personId
     * @return transaction
     * @throws CoreException
     */
    public RegistrationTransaction startRegistrationTransaction(String sessionId,
                                                                String personId) throws CoreException;

    /**
     * Retrieves person session with personId.
     *
     * @param sessionId session id
     * @param personId person id.
     * @return person session
     * @throws CoreException is thrown when person is not exist.
     */
    PersonSession readPerson(String sessionId,
                             String personId) throws CoreException;

    /**
     * Deletes person.
     *
     * @param sessionId  session id for deleting
     * @param personId person id for deleting
     * @throws CoreException is thrown when person is not exist.
     */
    void deletePerson(String sessionId,
                      String personId) throws CoreException;


    /**
     * Adds VoiceDynamicSample for the person.
     *
     * @param sessionId   session id for adding the VoiceSample
     * @param transactionId transaction id for adding the VoiceSample
     * @param voiceFile byte array is containing the VoiceFile
     * @param passphrase  passphrase which was spoken by person
     * @param gender person gender: 0 is men, 1 is women
     * @param channel
     * @throws CoreException is thrown when person is not exist.
     */
    void addVoiceDynamicFile(String sessionId,
                             String transactionId,
                             byte[] voiceFile,
                             String passphrase,
                             int gender,
                             int channel) throws CoreException;

    /**
     * Adds VoiceDynamicSample for the person.
     *
     * @param sessionId   session id for adding the VoiceSample
     * @param transactionId transaction id for adding the VoiceSample
     * @param voiceSample byte array is containing the VoiceSample
     * @param passphrase  passphrase which was spoken by person
     * @param gender person gender: 0 is men, 1 is women
     * @param samplingRate
     * @throws CoreException is thrown when person is not exist.
     */
    void addVoiceDynamicSample(String sessionId,
                               String transactionId,
                               byte[] voiceSample,
                               String passphrase,
                               int gender,
                               int samplingRate) throws CoreException;

    /**
     * Adds FaceFile for the person.
     *
     * @param sessionId  session id for adding the FaceModel
     * @param transactionId transaction id for adding the FaceModel
     * @param faceModel byte array is containing face model
     * @throws CoreException is thrown when person is not exist.
     */
    void addFaceFile(String sessionId,
                     String transactionId,
                     byte[] faceModel) throws CoreException;

    /**
     *
     * Start verification transaction
     *
     * @param sessionId
     * @param personId
     * @return
     * @throws CoreException
     */
    VerificationTransaction startVerificationTransaction(String sessionId,
                                                         String personId) throws CoreException;

    /**
     * Adds VoiceSample for the verification with gender.
     *
     * @param sessionId    current verification session
     * @param transaction  current virification transaction
     * @param voiceSample  byte array is containing the VoiceSample
     * @param gender       person gender: 0 is men, 1 is women
     * @throws CoreException is thrown when verification session is not exist.
     */
    void addDynamicVerificationVoiceSample(String sessionId,
                                           VerificationTransaction transaction,
                                           byte[] voiceSample,
                                           int gender,
                                           int samplingRate) throws CoreException;

    /**
     * Adds Video to server.
     *
     * @param sessionId current verification session
     * @param transactionId
     * @param passphrase
     * @param video byte array with video file
     * @throws CoreException
     */
    void addDynamicVerificationVideo(String sessionId,
                                     String transactionId,
                                     String passphrase,
                                     byte[] video) throws CoreException;

    /**
     * Verifies the person authenticity.
     *
     * @param sessionId   current verification session
     * @param transactionId current transaction id
     * @param closeFlag flag for closing session
     * @return true if verification is success, false otherwise
     * @throws CoreException is thrown when verification session is not exist.
     */
    boolean verify(String sessionId,
                   String transactionId,
                   boolean closeFlag) throws CoreException;

    /**
     *
     *
     *
     * @param sessionId   current verification session
     * @param transactionId current transaction id
     * @param closeFlag flag for closing session
     * @return score verify
     * @throws CoreException is thrown when verification session is not exist.
     */
    ScoreVerify scoreVerify(String sessionId,
                            String transactionId,
                            boolean closeFlag) throws CoreException;

    /**
     * Closes verification session.
     *
     * @param sessionId current verification session
     * @param transactionId current transaction id
     * @throws CoreException is thrown when verification session is not exist.
     */
    void closeVerification(String sessionId,
                           String transactionId) throws CoreException;
}
