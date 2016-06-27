package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * @author volobuev
 * @since 25.04.16
 */
public class CreatePersonTask extends ExceptionAsyncTask<String, Void, PersonSession> {

    private final ITransport transport;

    public CreatePersonTask(ITransport transport) {
        super();
        this.transport = transport;
    }

    @Override
    protected PersonSession doInBackground(String... params) {
        PersonSession personSession = null;
        try {
            personSession = transport.createPerson(params[0]);
        } catch (CoreException e) {
            exception = e;
        }
        return personSession;
    }
}

