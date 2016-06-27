package com.speechpro.onepass.core.sessions;

import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.core.exception.CoreException;

/**
 * Describes verification process.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public class VerificationSession {

    private final ITransport transport;
    // Verification uuid
    private final String     uuid;
    // Spoken phrase
    private       String     passphrase;
    // Person gender, 0 is men, 1 is women
    private int gender = 0;

    /**
     * Constructor.
     *
     * @param transport  api for rest interaction.
     * @param uuid       verification uuid
     * @param passphrase spoken phrase
     */
    public VerificationSession(ITransport transport, String uuid, String passphrase) {
        this.transport = transport;
        this.uuid = uuid;
        this.passphrase = passphrase;
    }

    /**
     * Retrieves person uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Retrieves person passphrase.
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * Retrieves person gender.
     */
    public int getGender() {
        return gender;
    }

    /**
     * Sets person gender.
     *
     * @param gender person gender
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * Sets passphrase.
     *
     * @param passphrase spoken phrase
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * Adds VoiceSample.
     *
     * @param voiceSample byte array is containing the VoiceSample
     */
    public void addVoiceSample(byte[] voiceSample, int samplingRate) throws CoreException {
        transport.addVerificationVoiceSample(this, voiceSample, gender, samplingRate);
    }

    /**
     * Adds VoiceFeature.
     *
     * @param voiceFeature byte array is containing the VoiceFeature
     */
    public void addVoiceFeature(byte[] voiceFeature) throws CoreException {
        transport.addVerificationVoiceFeature(this, voiceFeature);
    }

    /**
     * Adds FaceMode.
     *
     * @param faceModel byte array is containing the FaceMode.
     */
    public void addFaceModel(byte[] faceModel) throws CoreException {
        transport.addVerificationFaceModel(this, faceModel);
    }

    /**
     * Adds FaceSample.
     *
     * @param faceSample byte array is containing the FaceSample.
     */
    public void addFaceSample(byte[] faceSample) throws CoreException {
        transport.addVerificationFaceSample(this, faceSample);
    }

    /**
     * Adds Video.
     *
     * @param video byte array is containing the video.
     */
    public void addVideo(byte[] video) throws CoreException {
        transport.addVerificationVideo(this, video);
    }

    /**
     * Verifies the person authenticity.
     *
     * @param closeFlag flag for closing session
     */
    public boolean verify(boolean closeFlag) {
        try {
            return transport.verify(this, closeFlag);
        } catch (CoreException e) {
            return false;
        }
    }

    /**
     * Closes verification session.
     */
    public void close() throws CoreException {
        transport.closeVerification(this);
    }
}
