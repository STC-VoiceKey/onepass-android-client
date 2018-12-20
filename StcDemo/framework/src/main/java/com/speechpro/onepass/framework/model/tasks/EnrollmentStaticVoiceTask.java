package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author Alexander Grigal
 */
public class EnrollmentStaticVoiceTask extends ExceptionAsyncTask<VoiceSample, Void, Void> {
    private final RegistrationTransaction registrationTransaction;

    public EnrollmentStaticVoiceTask(RegistrationTransaction registrationTransaction) {
        super();
        this.registrationTransaction = registrationTransaction;
    }

    @Override
    protected Void doInBackground(VoiceSample... params) {
        if (registrationTransaction != null) {
            try {
                VoiceSample sample = params[0];
                registrationTransaction.addVoiceStaticSample(sample.getVoiceSample(),
                        sample.getSamplingRate());
            } catch (CoreException e) {
                exception = e;
            }
        }
        return null;
    }
}
