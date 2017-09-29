package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class VerificationResultTask extends ExceptionAsyncTask<Void, Void, Boolean>{

    private final VerificationTransaction verificationtransaction;

    public VerificationResultTask(VerificationTransaction verificationtransaction) {
        super();
        this.verificationtransaction = verificationtransaction;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
            return verificationtransaction.verify(true);
    }
}