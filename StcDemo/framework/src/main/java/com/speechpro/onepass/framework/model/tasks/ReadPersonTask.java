package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class ReadPersonTask extends ExceptionAsyncTask<String, Void, PersonSession> {

    private final ITransport transport;

    public ReadPersonTask(ITransport transport) {
        super();
        this.transport = transport;
    }

    @Override
    protected PersonSession doInBackground(String... params) {
        try {
            return transport.readPerson(params[0]);
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}
