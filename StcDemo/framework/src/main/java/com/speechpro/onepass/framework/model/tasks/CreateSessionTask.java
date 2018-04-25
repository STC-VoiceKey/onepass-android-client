package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * @author volobuev
 * @since 25.04.16
 */
public class CreateSessionTask extends ExceptionAsyncTask<String, Void, String> {

    private final ITransport transport;

    private final String username;
    private final String password;
    private final int domainId;


    public CreateSessionTask(ITransport transport, String username, String password, int domainId) {
        super();
        this.transport = transport;
        this.username = username;
        this.password = password;
        this.domainId = domainId;
    }

    @Override
    protected String doInBackground(String... params) {

        String session = null;

        try {
            session = transport.startSession(username, password, domainId);
        } catch (CoreException e) {
            exception = e;
        }
        return session;
    }
}

