package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.core.sessions.transactions.RegistrationTransaction;
import com.speechpro.onepass.framework.model.data.FaceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class EnrollmentFaceTask extends ExceptionAsyncTask<FaceSample, Void, Void> {

    private final RegistrationTransaction registrationTransaction;

    public EnrollmentFaceTask(RegistrationTransaction registrationTransaction) {
        super();
        this.registrationTransaction = registrationTransaction;
    }

    @Override
    protected Void doInBackground(FaceSample... params) {
        try {
            registrationTransaction.addFaceFile(params[0].getFaceSample());
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}
