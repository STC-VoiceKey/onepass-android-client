package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.VerificationSession;
import com.speechpro.onepass.framework.model.data.FaceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class VerificationFaceTask extends ExceptionAsyncTask<FaceSample, Void, Void> {
    private final VerificationSession verificationSession;

    public VerificationFaceTask(VerificationSession verificationSession) {
        super();
        this.verificationSession = verificationSession;
    }

    @Override
    protected Void doInBackground(FaceSample... params) {
        if (verificationSession != null) {
            try {
                FaceSample sample = params[0];
                verificationSession.addFaceSample(sample.getFaceSample());
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}