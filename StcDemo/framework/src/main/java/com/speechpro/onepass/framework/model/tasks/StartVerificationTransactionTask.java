package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.VerificationTransaction;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class StartVerificationTransactionTask extends ExceptionAsyncTask<String, Void, VerificationTransaction> {

    private final ITransport transport;

    public StartVerificationTransactionTask(ITransport transport) {
        super();
        this.transport = transport;
    }

    @Override
    protected VerificationTransaction doInBackground(String... params) {
        try {
            return transport.startVerificationTransaction(params[0], params[1]);
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}