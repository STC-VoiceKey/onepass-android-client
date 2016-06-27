package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.VerificationSession;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class DeleteVerificationSessionTask extends ExceptionAsyncTask<Void, Void, Void> {

    private final VerificationSession verificationSession;

    public DeleteVerificationSessionTask(VerificationSession verificationSession) {
        super();
        this.verificationSession = verificationSession;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            verificationSession.close();
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}