package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.framework.model.data.FaceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class EnrollmentFaceTask extends ExceptionAsyncTask<FaceSample, Void, Void> {

    private final PersonSession personSession;

    public EnrollmentFaceTask(PersonSession personSession) {
        super();
        this.personSession = personSession;
    }

    @Override
    protected Void doInBackground(FaceSample... params) {
        try {
            personSession.addFaceSample(params[0].getFaceSample());
        } catch (CoreException e) {
            exception = e;
        }
        return null;
    }
}
