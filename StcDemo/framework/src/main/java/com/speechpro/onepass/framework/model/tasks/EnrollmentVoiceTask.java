package com.speechpro.onepass.framework.model.tasks;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.sessions.PersonSession;
import com.speechpro.onepass.framework.model.data.VoiceSample;

/**
 * @author volobuev
 * @since 26.04.16
 */
public class EnrollmentVoiceTask extends ExceptionAsyncTask<VoiceSample, Void, Void> {
    private final PersonSession personSession;

    public EnrollmentVoiceTask(PersonSession personSession) {
        super();
        this.personSession = personSession;
    }

    @Override
    protected Void doInBackground(VoiceSample... params) {
        if (personSession != null) {
            try {
                VoiceSample sample = params[0];
                personSession.addVoiceSample(sample.getVoiceSample(), sample.getPassphrase(), sample.getSamplingRate());
            } catch (CoreException e) {
                exception = e;
            }
        }
        return null;
    }
}
