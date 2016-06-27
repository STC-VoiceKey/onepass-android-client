package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.VerificationSession;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class VerificationVoiceTask extends ExceptionAsyncTask<VoiceSample, Void, Void> {
    private final VerificationSession verificationSession;

    public VerificationVoiceTask(VerificationSession verificationSession) {
        super();
        this.verificationSession = verificationSession;
    }

    @Override
    protected Void doInBackground(VoiceSample... params) {
        try {
            VoiceSample sample = params[0];
            verificationSession.addVoiceSample(sample.getVoiceSample(), sample.getSamplingRate());
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}