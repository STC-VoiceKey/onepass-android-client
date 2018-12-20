package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class VerificationDynamicVoiceTask extends ExceptionAsyncTask<VoiceSample, Void, Void> {
    private final VerificationTransaction verificationtransaction;

    public VerificationDynamicVoiceTask(VerificationTransaction verificationTransaction) {
        super();
        this.verificationtransaction = verificationTransaction;
    }

    @Override
    protected Void doInBackground(VoiceSample... params) {
        try {
            VoiceSample sample = params[0];
            verificationtransaction.addDynamicVoiceSample(sample.getVoiceSample(), sample.getSamplingRate());
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}