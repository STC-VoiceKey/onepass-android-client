package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.framework.model.data.FaceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class VerificationFaceTask extends ExceptionAsyncTask<FaceSample, Void, Void> {
    private final VerificationTransaction verificationTransaction;

    public VerificationFaceTask(VerificationTransaction verificationTransaction) {
        super();
        this.verificationTransaction = verificationTransaction;
    }

    @Override
    protected Void doInBackground(FaceSample... params) {
        if (verificationTransaction != null) {
            try {
                FaceSample sample = params[0];
                verificationTransaction.addFaceFile(sample.getFaceSample());
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}