package com.speechpro.onepass.core.sessions;

/**
 * Created by alexander on 11.09.17.
 */

public class Model {

    private final Long id;
    private final String type;
    private final int samplesCount;

    public Model(Long id, String type, int samplesCount) {
        this.id = id;
        this.type = type;
        this.samplesCount = samplesCount;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getSamplesCount() {
        return samplesCount;
    }
}
