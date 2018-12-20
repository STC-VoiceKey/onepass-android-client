package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author Alexander Grigal
 */
public class VerificationStaticVoiceTask extends ExceptionAsyncTask<VoiceSample, Void, Void> {
    private final VerificationTransaction verificationtransaction;

    public VerificationStaticVoiceTask(VerificationTransaction verificationTransaction) {
        super();
        this.verificationtransaction = verificationTransaction;
    }

    @Override
    protected Void doInBackground(VoiceSample... params) {
        try {
            VoiceSample sample = params[0];
            verificationtransaction.addStaticVoiceSample(sample.getVoiceSample(), sample.getSamplingRate());
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}

