package com.speechpro.onepass.framework.model.tasks;

import android.os.AsyncTask;

import com.speechpro.android.session.session_library.SessionClientFactory;
import com.speechpro.android.session.session_library.exception.InternetConnectionException;
import com.speechpro.android.session.session_library.exception.RestException;

/**
 * @author volobuev
 * @since 25.04.16
 */
public class CreateSessionTask extends AsyncTask<String, Void, String> {

    private final SessionClientFactory.SessionClient sessionClient;

    private final String username;
    private final String password;
    private final int domainId;

    private Exception exception;

    public CreateSessionTask(SessionClientFactory.SessionClient sessionClient, String username, String password, int domainId) {
        super();
        this.sessionClient = sessionClient;
        this.username = username;
        this.password = password;
        this.domainId = domainId;
    }

    @Override
    protected String doInBackground(String... params) {
        String session = null;

        try {
            session = sessionClient.openSession(username, password, domainId);
        } catch (InternetConnectionException | RestException e) {
            exception = e;
        }

        return session;
    }

    public Exception getException() {
        return exception;
    }
}

