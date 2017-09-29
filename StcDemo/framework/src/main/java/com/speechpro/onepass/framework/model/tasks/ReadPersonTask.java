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
    private final String sessionId;
    private final String personId;

    public ReadPersonTask(ITransport transport, String sessionId, String personId) {
        super();
        this.transport = transport;
        this.sessionId = sessionId;
        this.personId = personId;
    }

    @Override
    protected PersonSession doInBackground(String... params) {
        try {
            return transport.readPerson(sessionId, personId);
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}
