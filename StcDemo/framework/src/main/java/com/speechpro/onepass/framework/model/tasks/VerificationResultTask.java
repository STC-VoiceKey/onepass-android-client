package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.sessions.VerificationSession;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class VerificationResultTask extends ExceptionAsyncTask<Void, Void, Boolean>{

    private final VerificationSession verificationSession;

    public VerificationResultTask(VerificationSession verificationSession) {
        super();
        this.verificationSession = verificationSession;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
            return verificationSession.verify(true);
    }
}