package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * Created by alexander on 07.08.17.
 */

public class StartRegistrationTransactionTask extends ExceptionAsyncTask<String, Void, RegistrationTransaction> {

    private final ITransport transport;

    public StartRegistrationTransactionTask(ITransport transport) {
        super();
        this.transport = transport;
    }

    @Override
    protected RegistrationTransaction doInBackground(String... params) {
        try {
            return transport.startRegistrationTransaction(params[0], params[1]);
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}