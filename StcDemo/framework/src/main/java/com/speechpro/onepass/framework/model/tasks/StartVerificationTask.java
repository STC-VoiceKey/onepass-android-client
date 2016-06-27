package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.VerificationSession;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class StartVerificationTask extends ExceptionAsyncTask<String, Void, VerificationSession> {

    private final ITransport transport;

    public StartVerificationTask(ITransport transport) {
        super();
        this.transport = transport;
    }

    @Override
    protected VerificationSession doInBackground(String... params) {
        try {
            return transport.startVerificationSession(params[0]);
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}