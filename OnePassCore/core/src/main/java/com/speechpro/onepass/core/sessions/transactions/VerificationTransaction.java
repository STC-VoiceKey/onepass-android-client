package com.speechpro.onepass.core.sessions.transactions;

import android.util.Pair;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * Created by alexander on 28.07.17.
 */

public class VerificationTransaction {
    private final ITransport transport;
    private final String sessionId;
    private final String transactionId;
    private String passphrase;

    public VerificationTransaction(ITransport transport,
                                   String sessionId,
                                   String transactionId,
                                   String passphrase) {
        this.transport = transport;
        this.sessionId = sessionId;
        this.transactionId = transactionId;
        this.passphrase = passphrase;
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

    public String getPassphrase() {
        return this.passphrase;
    }

    /**
     *
     * Set passphrase
     *
     * @param passphrase
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     *
     * Adds VoiceSample for the person.
     *
     * @param voiceSample byte array is containing the VoiceSample
     * @param samplingRate person gender: 0 is men, 1 is women
     * @throws CoreException is thrown when person is not exist.
     */
    public void addVoiceSample(byte[] voiceSample, int samplingRate) throws CoreException {
        transport.addDynamicVerificationVoiceSample(sessionId, this, voiceSample, samplingRate);
    }

    /**
     * Adds FaceFile for the person.
     *
     * @param faceModel byte array is containing face model
     * @throws CoreException is thrown when person is not exist.
     */
    public void addFaceFile(byte[] faceModel) throws CoreException {
        transport.addVerivicationFaceFile(sessionId, transactionId, faceModel);
    }

    /**
     *
     * Adds video for the person
     *
     * @param video byte array is containing face model
     * @throws CoreException is thrown when person is not exist.
     */
    public void addDynamicVerificationVideo(byte[] video) throws CoreException {
        transport.addDynamicVerificationVideo(sessionId, this.transactionId, this.getPassphrase(), video);
    }

    /**
     *
     * Verifies the person authenticity.
     *
     * @param closeFlag flag for closing session
     * @return true if verification is success, false otherwise
     */
    public boolean verify(boolean closeFlag) {
        try {
            return transport.verify(sessionId, transactionId, closeFlag);
        } catch (CoreException e) {
            return false;
        }
    }

    /**
     *
     * Verifies the person authenticity.
     *
     * @param closeFlag flag for closing session
     * @return pair with first true if verification is success, false otherwise and second object message
     */
    public Pair<Boolean, String> verifyWithMessage(boolean closeFlag) {
        try {
            return transport.verifyWithMessage(sessionId, transactionId, closeFlag);
        } catch (CoreException e) {
            return new Pair<>(false, e.getMessage());
        }
    }

    /**
     *
     * Close verification
     *
     * @throws CoreException
     */
    public void closeVerification() throws CoreException {
        transport.closeVerification(sessionId, transactionId);
    }
}
