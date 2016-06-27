package com.speechpro.onepass.framework.model.tasks;

import android.os.AsyncTask;
import com.speechpro.onepass.core.exception.CoreException;

/**
 * @author volobuev
 * @since 25.04.16
 */
public abstract class ExceptionAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
    protected CoreException exception;

    public CoreException getException() {
        return exception;
    }
}
