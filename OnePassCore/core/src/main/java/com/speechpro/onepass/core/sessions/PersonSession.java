package com.speechpro.onepass.core.sessions;

import com.speechpro.onepass.core.transport.ITransport;
import com.speechpro.onepass.core.exception.CoreException;

/**
 * Describes general session in OnePass and gives interaction methods for that.
 *
 * @author volobuev
 * @since 24.09.2015.
 */
public class PersonSession {

    private final ITransport transport;
    // Person
    private final String personId;
    private final boolean isFullEnroll;
    // Person gender, 0 is men, 1 is women
    private int gender = 0;

    /**
     * Constructor.
     *  @param transport api for rest interaction.
     * @param personId  person personId in database.
     * @param isFullEnroll
     */
    public PersonSession(ITransport transport, String personId, boolean isFullEnroll) {
        if (transport == null) {
            throw new IllegalArgumentException("ITransport parameter is null.");
        }
        if (personId == null) {
            throw new IllegalArgumentException("personId parameter is null.");
        }
        this.transport = transport;
        this.personId = personId;
        this.isFullEnroll = isFullEnroll;
    }

    /**
     * Retrieves person id.
     */
    public String getId() {
        return personId;
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

    public boolean isFullEnroll() {
        return isFullEnroll;
    }

    /**
     * Deletes person.
     */
    public void delete() throws CoreException {
        transport.deletePerson(personId);
    }

    /**
     * Adds VoiceSample.
     *
     * @param voiceSample byte array is containing the VoiceSample
     * @param passphrase  passphrase which was spoken by person
     */
    public void addVoiceSample(byte[] voiceSample, String passphrase, Integer samplingRate) throws CoreException {
        transport.addVoiceSample(personId, voiceSample, passphrase, gender, samplingRate);
    }

    /**
     * Adds VoiceFeature.
     *
     * @param voiceFeature byte array is containing the VoiceFeature
     */
    public void addVoiceFeature(byte[] voiceFeature) throws CoreException {
        transport.addVoiceFeature(personId, voiceFeature);
    }

    /**
     * Deletes voice.
     */
    public void deleteVoice() throws CoreException {
        transport.deleteVoice(personId);
    }

    /**
     * Adds FaceMode.
     *
     * @param faceModel byte array is containing the FaceMode
     */
    public void addFaceModel(byte[] faceModel) throws CoreException {
        transport.addFaceModel(personId, faceModel);
    }

    /**
     * Adds FaceSample.
     *
     * @param faceSample byte array is containing the FaceSample
     */
    public void addFaceSample(byte[] faceSample) throws CoreException {
        transport.addFaceSample(personId, faceSample);
    }

    /**
     * Deletes face.
     */
    public void deleteFace() throws CoreException {
        transport.deleteFace(personId);
    }

    /**
     * Starts verification session.
     */
    public VerificationSession startVerificationSession() throws CoreException {
        return transport.startVerificationSession(personId);
    }
}
