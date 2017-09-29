package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class EnrollmentVoiceTask extends ExceptionAsyncTask<VoiceSample, Void, Void> {
    private final RegistrationTransaction registrationTransaction;

    public EnrollmentVoiceTask(RegistrationTransaction registrationTransaction) {
        super();
        this.registrationTransaction = registrationTransaction;
    }

    @Override
    protected Void doInBackground(VoiceSample... params) {
        if (registrationTransaction != null) {
            try {
                VoiceSample sample = params[0];
                registrationTransaction.addVoiceDynamicSample(sample.getVoiceSample(),
                        sample.getPassphrase(),
                        sample.getSamplingRate());
            } catch (CoreException e) {
                exception = e;
            }
        }
        return null;
    }
}
