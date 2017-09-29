package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class DeleteVerificationTransactionTask extends ExceptionAsyncTask<Void, Void, Void> {

    private final VerificationTransaction verificationTransaction;

    public DeleteVerificationTransactionTask(VerificationTransaction verificationTransaction) {
        super();
        this.verificationTransaction = verificationTransaction;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            verificationTransaction.closeVerification();
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}