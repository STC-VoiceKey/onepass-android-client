package com.speechpro.onepass.core.sessions;

import com.speechpro.onepass.core.exception.CoreException;
import com.speechpro.onepass.core.transport.ITransport;

import java.util.Set;

/**
 * Created by alexander on 07.08.17.
 */

public class PersonSession {

    private final ITransport transport;
    private final String sessionId;
    private final String personId;
    private final Set<Model> models;
    private final boolean isFullEnroll;

    public PersonSession(ITransport transport, String sessionId, String personId,
                         Set<Model> models, boolean isFullEnroll) {
        if(transport == null) {
            throw new IllegalArgumentException("ITransport parameter is null.");
        } else if(sessionId == null) {
            throw new IllegalArgumentException("sessionId parameter is null.");
        } else if(personId == null) {
            throw new IllegalArgumentException("personId parameter is null.");
        } else {
            this.transport = transport;
            this.sessionId = sessionId;
            this.personId = personId;
            this.models = models;
            this.isFullEnroll = isFullEnroll;
        }
    }

    public String getId() {
        return this.personId;
    }

    public Set<Model> getModels() {
        return models;
    }

    public boolean isFullEnroll() {
        return this.isFullEnroll;
    }

    public void delete() throws CoreException {
        this.transport.deletePerson(this.sessionId, this.personId);
    }

}
