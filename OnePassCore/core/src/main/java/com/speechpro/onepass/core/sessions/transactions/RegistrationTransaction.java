package com.speechpro.onepass.core.sessions.transactions;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * Created by alexander on 28.07.17.
 */

public class RegistrationTransaction {
    private final ITransport transport;
    private final String sessionId;
    private String transactionId;
    private final String personId;
    private final boolean isFullEnroll;

    public RegistrationTransaction(ITransport transport,
                                   String sessionId,
                                   String transactionId,
                                   String personId,
                                   boolean isFullEnroll) {
        if (transport == null) {
            throw new IllegalArgumentException("ITransport parameter is null.");
        } else if (sessionId == null) {
            throw new IllegalArgumentException("sessionId parameter is null.");
        } else if (transactionId == null) {
            throw new IllegalArgumentException("transactionId parameter is null.");
        } else if (personId == null) {
            throw new IllegalArgumentException("personId parameter is null.");
        } else {
            this.transport = transport;
            this.sessionId = sessionId;
            this.transactionId = transactionId;
            this.personId = personId;
            this.isFullEnroll = isFullEnroll;
        }
    }

    /**
     * Retrieves current session
     *
     * @return current session id
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Retrieves current transaction
     *
     * @return current transaction id
     */
    public String getTransactionId() {
        return this.transactionId;
    }

    /**
     * Set transaction id
     *
     * @param transactionId transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Retrieves id for current transaction
     *
     * @return id
     */
    public String getId() {
        return this.personId;
    }

    /**
     * Retrieves flag isFullEnroll
     *
     * @return
     */
    public boolean isFullEnroll() {
        return this.isFullEnroll;
    }

    /**
     * Delete person
     *
     * @throws CoreException
     */
    public void delete() throws CoreException {
        transport.deletePerson(sessionId, personId);
    }

    /**
     * Adds VoiceDynamicSample for the person.
     *
     * @param voiceSample  byte array is containing the VoiceSample
     * @param passphrase   passphrase which was spoken by person
     * @param samplingRate sampling rate
     * @throws CoreException is thrown when person is not exist.
     */
    public void addVoiceDynamicSample(byte[] voiceSample,
                                      String passphrase,
                                      Integer samplingRate) throws CoreException {
        transport.addVoiceDynamicSample(this.sessionId,
                this.transactionId,
                voiceSample,
                passphrase,
                samplingRate.intValue());
    }

    /**
     * Adds VoiceStaticSample for the person.
     *
     * @param voiceSample  byte array is containing the VoiceSample
     * @param samplingRate sampling rate
     * @throws CoreException is thrown when person is not exist.
     */
    public void addVoiceStaticSample(byte[] voiceSample,
                                      Integer samplingRate) throws CoreException {
        transport.addVoiceStaticSample(this.sessionId,
                this.transactionId,
                voiceSample,
                samplingRate);
    }

    /**
     * Adds VoiceDynamicFile for the person.
     *
     * @param voiceFile  byte array is containing the VoiceFile
     * @param passphrase   passphrase which was spoken by person
     * @param samplingRate sampling rate
     * @throws CoreException is thrown when person is not exist.
     */
    public void addVoiceDynamicFile(byte[] voiceFile,
                                    String passphrase,
                                    Integer samplingRate) throws CoreException {
        transport.addVoiceDynamicFile(this.sessionId,
                this.transactionId,
                voiceFile,
                passphrase,
                0);
    }

    /**
     * Adds FaceFile for the person.
     *
     * @param faceModel byte array is containing face model
     * @throws CoreException is thrown when person is not exist.
     */
    public void addFaceFile(byte[] faceModel) throws CoreException {
        transport.addFaceFile(sessionId, transactionId, faceModel);
    }

}
