package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.transport.ITransport;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class DeletePersonTask extends ExceptionAsyncTask<String, Void, Void> {

    private final ITransport transport;

    public DeletePersonTask(ITransport transport) {
        super();
        this.transport = transport;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            transport.deletePerson(params[0]);
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}
